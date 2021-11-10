package com.airbus.retex.service.importer;

import com.airbus.retex.config.RetexConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Order(25)
public class DataImportListener {

    @Autowired
    private RetexConfig retexConfig;

    @Autowired
    List<IDataImporter> importers;

    @Scheduled(fixedDelay = 60*1000) // restart after 1 minute (60*1000 milli secondes)
    public synchronized void start() {
        log.info("Starting importing data ...");

        int success = 0;
        int errors = 0;
        for (IDataImporter importer : importers) {
            try {
                IDataImporter.Status status = importer.processImport();
                switch (status) {
                    case Success:
                        ++success;
                        break;
                    case Skipped:
                        // nop
                        break;
                    default:
                        ++errors;
                        break;
                }
            } catch (Exception exception) {
                log.error("An exception has been thrown during import",  exception);
                return;
            }
        }

        log.info(String.format("Import data done: %d success(es), %d error(s)", success, errors));
    }
}
