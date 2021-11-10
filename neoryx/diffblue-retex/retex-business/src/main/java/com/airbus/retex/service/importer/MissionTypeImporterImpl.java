package com.airbus.retex.service.importer;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.mission.MissionType;
import com.airbus.retex.model.mission.MissionTypeFields;
import com.airbus.retex.persistence.mission.MissionTypeRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import com.airbus.retex.service.importer.exception.InvalidEntryException;
import com.airbus.retex.service.translate.ITranslateService;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(55)
public class MissionTypeImporterImpl extends AbstractDataImporter {

    @Autowired
    private ITranslateService translateService;
    @Autowired
    private MissionTypeRepository missionTypeRepository;

    private static final String FIELD_MISSION_FR = "name_FR";
    private static final String FIELD_MISSION_EN = "name_EN";

    public MissionTypeImporterImpl() {
        super("mission_type.csv", new String[]{FIELD_MISSION_FR, FIELD_MISSION_EN});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String nameFr = getAndCheckUniquenessOfTranslatedField(row, FIELD_MISSION_FR, Language.FR, MissionTypeFields.FIELD_NAME);
        String nameEn = getAndCheckUniquenessOfTranslatedField(row, FIELD_MISSION_EN, Language.EN, MissionTypeFields.FIELD_NAME);

        MissionType missionType = new MissionType();
        missionTypeRepository.save(missionType);
        translateService.saveFieldValue(missionType, MissionTypeFields.FIELD_NAME, Language.FR, nameFr);
        translateService.saveFieldValue(missionType, MissionTypeFields.FIELD_NAME, Language.EN, nameEn);
    }

    protected String getAndCheckUniquenessOfTranslatedField(CSVRecord row, String rowField, Language lang, String field) throws DataImporterException {
        String value = row.get(rowField);
        if (value.isBlank()) {
            throw new InvalidEntryException(String.format("%s=%s", rowField, value));
        }
        if (!missionTypeRepository.findAll(translateFilter(field, lang, value)).isEmpty()) {
            throw new EntryAlreadyExistsException(String.format("%s=%s", rowField, value));
        }
        return value;
    }
}
