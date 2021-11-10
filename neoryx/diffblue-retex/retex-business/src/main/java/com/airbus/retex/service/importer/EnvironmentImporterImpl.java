package com.airbus.retex.service.importer;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.environment.Environment;
import com.airbus.retex.model.environment.EnvironmentFields;
import com.airbus.retex.persistence.environment.EnvironmentRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(30)
public class EnvironmentImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private EnvironmentRepository environmentRepository;

    private static final String FIELD_ENVIRONMENT_FR = "name_FR";
    private static final String FIELD_ENVIRONMENT_EN = "name_EN";

    public EnvironmentImporterImpl() {
        super("environment.csv", new String[]{FIELD_ENVIRONMENT_FR, FIELD_ENVIRONMENT_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_ENVIRONMENT_FR, Language.FR, EnvironmentFields.FIELD_NAME);
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_ENVIRONMENT_EN, Language.EN, EnvironmentFields.FIELD_NAME);

        Environment environment = new Environment();
        environmentRepository.save(environment);
        translateService.saveFieldValue(environment, EnvironmentFields.FIELD_NAME, Language.FR, nameFr);
        translateService.saveFieldValue(environment, EnvironmentFields.FIELD_NAME, Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!environmentRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }

}
