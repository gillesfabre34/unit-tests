package com.airbus.retex.service.importer;

import com.airbus.retex.model.material.Material;
import com.airbus.retex.persistence.material.MaterialRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class MaterialImporterImpl extends AbstractDataImporter {

    @Autowired
    private MaterialRepository materialRepository;

    private static final String FIELD_CODE = "code";

    public MaterialImporterImpl() {
        super("material.csv", new String[]{FIELD_CODE});
    }

    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String code = row.get(FIELD_CODE);
        if (materialRepository.findByCode(code).isPresent()) {
            throw new EntryAlreadyExistsException(row.toMap());
        }
        materialRepository.save(new Material(code));
    }
}
