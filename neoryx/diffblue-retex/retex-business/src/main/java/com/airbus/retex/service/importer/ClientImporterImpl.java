package com.airbus.retex.service.importer;

import com.airbus.retex.model.client.Client;
import com.airbus.retex.persistence.client.ClientRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
public class ClientImporterImpl extends AbstractDataImporter {
    @Autowired
    private ClientRepository clientRepository;

    private static final String FIELD_NAME = "name";

    public ClientImporterImpl() {
        super("client.csv", new String[]{FIELD_NAME});
    }


    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {

        String name = row.get(FIELD_NAME);
        if (clientRepository.existsByName(name)) {
            throw new EntryAlreadyExistsException(row.toMap());
        }

        Client c=new Client();
        c.setName(name);
        clientRepository.save(c);
    }

}
