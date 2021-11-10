package com.airbus.retex.service.importer;

import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.persistence.origin.OriginRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(65)
public class OriginImporter extends AbstractDataImporter {
    @Autowired
    private OriginRepository repository;

    private static final String FIELD_NAME = "name";
    private static final String FIELD_COLOR = "color";

    public OriginImporter() {
        super("origin.csv", new String[]{FIELD_NAME, FIELD_COLOR});
    }

    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String name = row.get(FIELD_NAME);
        if (repository.findByName(name).isPresent()) {
            throw new EntryAlreadyExistsException(row.toMap());
        }

        Origin item = new Origin();
        item.setName(name);
        item.setColor(row.get(FIELD_COLOR));
        repository.save(item);
    }
}
