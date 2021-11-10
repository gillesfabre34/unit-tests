package com.airbus.retex.service.importer;

import com.airbus.retex.business.dto.inspection.InspectionEnumFields;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.persistence.inspection.InspectionRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(45)
public class InspectionImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private InspectionRepository inspectionRepository;

    private static final String FIELD_VALUE = "value";
    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";

    public InspectionImporterImpl() {
        super("inspection.csv", new String[]{FIELD_VALUE, FIELD_NAME_FR, FIELD_NAME_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String value = row.get(FIELD_VALUE);
        if (inspectionRepository.findByValue(value).isPresent()) {
            throw new EntryAlreadyExistsException(row.toMap());
        }
        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, InspectionEnumFields.name.name());
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, InspectionEnumFields.name.name());

        Inspection inspection = new Inspection();
        inspection.setValue(value);
        inspectionRepository.save(inspection);
        translateService.saveFieldValue(inspection, InspectionEnumFields.name.name(), Language.FR, nameFr);
        translateService.saveFieldValue(inspection, InspectionEnumFields.name.name(), Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!inspectionRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
