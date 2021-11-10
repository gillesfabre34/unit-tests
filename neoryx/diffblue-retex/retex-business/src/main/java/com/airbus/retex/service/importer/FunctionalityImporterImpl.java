package com.airbus.retex.service.importer;

import com.airbus.retex.business.dto.functionality.FunctionalityFields;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(40)
public class FunctionalityImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private FunctionalityRepository functionalityRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";

    public FunctionalityImporterImpl() {
        super("functionality.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, FunctionalityFields.NAME);
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, FunctionalityFields.NAME);

        Functionality functionality = new Functionality();
        functionalityRepository.save(functionality);
        translateService.saveFieldValue(functionality, FunctionalityFields.NAME, Language.FR, nameFr);
        translateService.saveFieldValue(functionality, FunctionalityFields.NAME, Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!functionalityRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
