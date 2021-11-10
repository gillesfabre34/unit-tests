package com.airbus.retex.service.importer;

import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.persistence.airbus.AirbusEntityRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(5)
public class AirbusEntityImporterImpl extends AbstractDataImporter {

    @Autowired
    private AirbusEntityRepository airbusEntityRepository;
    private static final String FIELD_CODE = "code";
    private static final String FIELD_COUNTRY_NAME = "country_name";

    public AirbusEntityImporterImpl() {
        super("airbus_entity.csv", new String[]{FIELD_CODE, FIELD_COUNTRY_NAME});
    }

    /**
     *
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String code = row.get(FIELD_CODE);
        String countryName = row.get(FIELD_COUNTRY_NAME);
        if (airbusEntityRepository.existsByCodeAndCountryName(code, countryName)) {
            throw new EntryAlreadyExistsException(row.toMap());
        }

        AirbusEntity airbusEntity = new AirbusEntity();
        airbusEntity.setCode(code);
        airbusEntity.setCountryName(countryName);
        airbusEntity.setState(EnumActiveState.ACTIVE);
        airbusEntityRepository.save(airbusEntity);
    }

}
