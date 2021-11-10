package com.airbus.retex.service.importer;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.translation.Translate;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDataImporter implements IDataImporter {

    @Autowired
    private RetexConfig retexConfig;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private static final String FAILED_EXTENSION = ".failed";
    private static final String DONE_EXTENSION = ".done";

    private final String importSource;
    private String[] headers;


    public AbstractDataImporter(String importSource, String[] headers) {
        this.importSource = importSource;
        this.headers = headers;
    }


    @Override
    public Status processImport() throws Exception {

        String importFilename = retexConfig.getImportsFolder() + importSource;

        File importFile = new File(importFilename);
        if (!importFile.exists()) {
            return Status.Skipped;
        }

        Status status = Status.Unknown;
        TransactionStatus tx = platformTransactionManager.getTransaction(null);

        try {
            long entryNumber = processImportInternal(importFile);
            platformTransactionManager.commit(tx);
            log.info(String.format("Import successful of %d entry(ies) from %s", entryNumber, importFile.getName()));
            status = Status.Success;
        } catch (DataImporterException ex) {
            platformTransactionManager.rollback(tx);
            log.error(String.format("Import error at %s:%d : %s", importFile.getName(), ex.getLineNumber(), ex.getMessage()), ex.getCause());
            status = Status.Error;
        } catch (Exception ex) {
            platformTransactionManager.rollback(tx);
            log.error("An exception has been thrown during import of " + importFile.getName(), ex);
            status = Status.Error;
        } finally {
            if (status == Status.Unknown) {
                // an exception occurred in the catch
                try {
                    renameFile(importFile, FAILED_EXTENSION);
                } catch (Exception ex) {
                    // do not override the original exception
                    log.error("renameFile wanted to throw:", ex);
                }
            }
        }

        if (status != Status.Unknown) {
            String extension = (status == Status.Success ? DONE_EXTENSION : FAILED_EXTENSION);
            renameFile(importFile, extension);
        }
        return status;
    }


    private void renameFile(File importFile, String extension) throws Exception {
        File renameTo = generateUniqueFile(importFile.getPath() + extension);
        if (!importFile.renameTo(renameTo)) {
            throw new Exception("Can't rename to " + renameTo.getName());
        }
    }


    private File generateUniqueFile(String filename) throws Exception {

        DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String date = df.format(new Date());

        for (int retry = 0; retry < 1000; ++retry ) {
            String retrySuffix = retry != 0 ? ("-" + Integer.toString(retry)) : "";
            String fn = String.format("%s.%s%s", filename, date, retrySuffix);
            File ret = new File(fn);
            if (!ret.exists()) {
                return ret;
            }
        }

        // very unlikely
        throw new Exception("Can't generate a unique filename for " + filename);
    }


    private long processImportInternal(File importFile) throws Exception {

        CSVFormat fmt = CSVFormat.EXCEL
                .withDelimiter(retexConfig.getImportsDelimiter())
                .withQuote(retexConfig.getImportsQuote())
                .withAllowDuplicateHeaderNames(false)
                .withAllowMissingColumnNames(false)
                .withHeader(headers)
                .withTrim()
                .withFirstRecordAsHeader();

        Reader in = new FileReader(importFile);
        CSVParser parser = fmt.parse(in);

        int recordSize = parser.getHeaderNames().size();

        try {
            for (CSVRecord record : parser) {
                if (record.size() != recordSize) {
                    throw new InvalidEntryException(record.toMap());
                }
                persistBatch(record);
                if (record.getRecordNumber() % 100 == 99) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        } catch (DataImporterException ex) {
            if (ex.getLineNumber() == -1) {
                ex.setLineNumber(parser.getCurrentLineNumber());
            }
            throw ex;
        } catch (Exception ex) {
            long lineNumber = parser.getCurrentLineNumber();
            String msg = ex.getMessage();
            Exception cause = ex;

            if (ex instanceof IllegalArgumentException && ex.getMessage().startsWith("Mapping for name not found, expected one of")) {
                lineNumber = 1;
                msg = "Invalid header field";
                cause = null;
            }

            throw new DataImporterException(lineNumber, msg, cause);
        }
        return parser.getRecordNumber();
    }


    protected static <T> Specification<T> translateFilter(String fieldName, Language lang, String fieldValue) {
        return (root, query, cb) -> {
            Join<T, Translate> join = root.join("translates", JoinType.LEFT);
            return cb.and(
                    cb.equal(join.get("value"), fieldValue),
                    cb.equal(join.get("field"), fieldName),
                    cb.equal(join.get("language"), lang)
            );
        };
    }


    protected abstract void persistBatch(CSVRecord row) throws DataImporterException;
}
