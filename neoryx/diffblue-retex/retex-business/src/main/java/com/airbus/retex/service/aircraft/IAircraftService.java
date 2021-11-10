package com.airbus.retex.service.aircraft;


import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Component
public interface IAircraftService {

    List<AircraftFamilyLightDto> getAllAircraftFamilies();

    List<AircraftTypeLightDto> getAllAircraftType(Long aircraftFamilyId);

    List<AircraftVersionLightDto> getAllAircraftVersion(Collection<Long> aircraftTypeIds);

    Set<Long> findInvalidAircraftTypeIds(Long aircraftFamilyId, Collection<Long> aircraftTypeIds);

    Set<Long> findInvalidAircraftVersionIds(Collection<Long> aircraftTypeIds, Collection<Long> aircraftVersionIds);
}
