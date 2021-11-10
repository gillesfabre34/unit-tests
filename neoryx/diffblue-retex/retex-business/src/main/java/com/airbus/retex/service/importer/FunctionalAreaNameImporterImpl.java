package com.airbus.retex.service.importer;

import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.functional.FunctionalAreaName;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaNameRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(35)
public class FunctionalAreaNameImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private FunctionalAreaNameRepository functionalAreaNameRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";

    public FunctionalAreaNameImporterImpl() {
        super("functional_area_name.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, FunctionalAreaNameFieldsEnum.name.name());
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, FunctionalAreaNameFieldsEnum.name.name());

        FunctionalAreaName functionalAreaName = new FunctionalAreaName();
        functionalAreaNameRepository.save(functionalAreaName);
        translateService.saveFieldValue(functionalAreaName, FunctionalAreaNameFieldsEnum.name.name(), Language.FR, nameFr);
        translateService.saveFieldValue(functionalAreaName, FunctionalAreaNameFieldsEnum.name.name(), Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!functionalAreaNameRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
