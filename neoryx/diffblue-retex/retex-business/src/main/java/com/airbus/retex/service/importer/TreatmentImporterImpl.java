package com.airbus.retex.service.importer;

import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.airbus.retex.business.dto.treatment.TreatmentFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.treatment.Treatment;
import com.airbus.retex.persistence.treatment.TreatmentRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;

@Component
@Order(75)
public class TreatmentImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private TreatmentRepository treatmentRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";

    public TreatmentImporterImpl() {
        super("treatment.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, TreatmentFieldsEnum.name.name());
        getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, TreatmentFieldsEnum.name.name());

        Treatment treatment = new Treatment();
        treatmentRepository.save(treatment);
        translateService.saveFieldValue(treatment, TreatmentFieldsEnum.name.name(), Language.FR, row.get(FIELD_NAME_FR));
        translateService.saveFieldValue(treatment, TreatmentFieldsEnum.name.name(), Language.EN, row.get(FIELD_NAME_EN));
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!treatmentRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
