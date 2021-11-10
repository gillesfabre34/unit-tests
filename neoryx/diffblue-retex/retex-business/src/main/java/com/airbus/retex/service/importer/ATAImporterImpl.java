package com.airbus.retex.service.importer;

import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.persistence.ata.ATARepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(15)
public class ATAImporterImpl extends AbstractDataImporter {

    @Autowired
    private ATARepository ataRepository;

    private static final String FIELD_CODE = "code";

    public ATAImporterImpl() {
        super("ata.csv", new String[]{FIELD_CODE});
    }

    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String code = row.get(FIELD_CODE);
        if (ataRepository.findByCode(code).isPresent()) {
            throw new EntryAlreadyExistsException(row.toMap());
        }
        ataRepository.save(new ATA(code));
    }
}
