package com.airbus.retex.service.importer;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.model.part.PartDesignationFields;
import com.airbus.retex.persistence.part.PartDesignationRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(70)
public class PartDesignationImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private PartDesignationRepository partDesignationRepository;

    private static final String FIELD_DESIGNATION_FR = "designation_FR";
    private static final String FIELD_DESIGNATION_EN = "designation_EN";

    public PartDesignationImporterImpl() {
        super("part_designation.csv", new String[]{FIELD_DESIGNATION_FR, FIELD_DESIGNATION_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_DESIGNATION_FR, Language.FR, PartDesignationFields.FIELD_DESIGNATION);
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_DESIGNATION_EN, Language.EN, PartDesignationFields.FIELD_DESIGNATION);

        PartDesignation partDesignation = new PartDesignation();
        partDesignationRepository.save(partDesignation);
        translateService.saveFieldValue(partDesignation, PartDesignationFields.FIELD_DESIGNATION, Language.FR, nameFr);
        translateService.saveFieldValue(partDesignation, PartDesignationFields.FIELD_DESIGNATION, Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!partDesignationRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
