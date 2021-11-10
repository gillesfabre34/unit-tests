package com.airbus.retex.service.importer;

import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.service.importer.exception.DataImporterException;
import com.airbus.retex.service.importer.exception.EntryAlreadyExistsException;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(10)
public class AircraftImporterImpl extends AbstractDataImporter {

    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;

    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;

    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;

    private static final String FIELD_AIRCRAFT_TYPE = "aircraft_type";
    private static final String FIELD_AIRCRAFT_FAMILY = "aircraft_family";
    private static final String FIELD_AIRCRAFT_VERSION = "aircraft_version";

    public AircraftImporterImpl() {
        super("aircraft.csv", new String[]{FIELD_AIRCRAFT_TYPE, FIELD_AIRCRAFT_FAMILY, FIELD_AIRCRAFT_VERSION});
    }

    /**
     * @param row
     */
    @Override
    protected void persistBatch(CSVRecord row) throws DataImporterException {
        String family = row.get(FIELD_AIRCRAFT_FAMILY);
        String type = row.get(FIELD_AIRCRAFT_TYPE);
        String version = row.get(FIELD_AIRCRAFT_VERSION);
        AircraftFamily aircraftFamily;
        AircraftType aircraftType;
        AircraftVersion aircraftVersion;

        Optional<AircraftFamily> optAircraftFamily = aircraftFamilyRepository.findByName(family);

        if (optAircraftFamily.isPresent()) {
            aircraftFamily = optAircraftFamily.get();
            Optional<AircraftType> optAircraftType = aircraftFamily.getAircraftTypes().stream().filter(item -> item.getName().equals(type)).findFirst();
            if (optAircraftType.isPresent()) {
                aircraftType = optAircraftType.get();
                Optional<AircraftVersion> optAircraftVersion = aircraftType.getAircraftVersions().stream().filter(item -> item.getName().equals(version)).findFirst();
                if (optAircraftVersion.isPresent()) {
                    throw new EntryAlreadyExistsException(row.toMap());
                } else {
                    aircraftVersion = new AircraftVersion(version);
                }
            } else {
                aircraftType = new AircraftType(type);
                aircraftVersion = new AircraftVersion(version);
            }
        } else {
            aircraftFamily = new AircraftFamily(family);
            aircraftType = new AircraftType(type);
            aircraftVersion = new AircraftVersion(version);
        }
        newEntry(aircraftFamily, aircraftType, aircraftVersion);
    }

    private void newEntry(AircraftFamily aircraftFamily, AircraftType aircraftType, AircraftVersion aircraftVersion) {
        // FIXME : It is not clean to modify aircraftFamily ...use new AricraftFamily variable at this method
        // If we set the method params final we will get an error here !
        aircraftFamily = aircraftFamilyRepository.save(aircraftFamily);

        aircraftType.setAircraftFamily(aircraftFamily);
        aircraftType.setAircraftFamilyId(aircraftFamily.getId());
        aircraftType = aircraftTypeRepository.save(aircraftType);

        aircraftVersion.setAircraftType(aircraftType);
        aircraftVersion.setAircraftTypeId(aircraftType.getId());
        aircraftVersionRepository.save(aircraftVersion);

        aircraftType.addAircraftVersion(aircraftVersion);
        aircraftTypeRepository.save(aircraftType);

        aircraftFamily.addAircraftType(aircraftType);
        aircraftFamilyRepository.save(aircraftFamily);

    }

}
