package com.airbus.retex.service.importer;

import com.airbus.retex.business.dto.measureUnit.MeasureUnitsFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)//FIXME Put all import order value in one constant class to have a global view of import ordering
public class MeasureUnitImporterImpl extends AbstractDataImporter {
    @Autowired
    private ITranslateService translateService;
    @Autowired
    private MeasureUnitRepository measureUnitRepository;

    private static final String FIELD_NAME_FR = "name_FR";
    private static final String FIELD_NAME_EN = "name_EN";

    public MeasureUnitImporterImpl() {
        super("measure_unit.csv", new String[]{FIELD_NAME_FR, FIELD_NAME_EN});
    }

    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_FR, Language.FR, MeasureUnitsFieldsEnum.name.name());
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_NAME_EN, Language.EN, MeasureUnitsFieldsEnum.name.name());

        MeasureUnit measureUnit = new MeasureUnit();
        measureUnitRepository.save(measureUnit);
        translateService.saveFieldValue(measureUnit, MeasureUnitsFieldsEnum.name.name(), Language.FR, nameFr);
        translateService.saveFieldValue(measureUnit, MeasureUnitsFieldsEnum.name.name(), Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!measureUnitRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
