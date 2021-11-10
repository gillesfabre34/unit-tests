package com.airbus.retex.service.impl.aircraft;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.model.admin.aircraft.AircraftFamily;
import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.service.aircraft.IAircraftService;

@Service
@Transactional(rollbackFor = Exception.class)
public class AircraftServiceImpl implements IAircraftService {

    @Autowired
    private AircraftFamilyRepository aircraftFamilyRepository;

    @Autowired
    private AircraftTypeRepository aircraftTypeRepository;

    @Autowired
    private AircraftVersionRepository aircraftVersionRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<AircraftFamilyLightDto> getAllAircraftFamilies() {
        List<AircraftFamily> aircraftFamilies= aircraftFamilyRepository.findAll();
		return dtoConverter.toDtos(aircraftFamilies, AircraftFamilyLightDto.class);
    }

    @Override
    public List<AircraftTypeLightDto> getAllAircraftType(Long aircraftFamily) {
        List<AircraftType> aircraftTypes =  aircraftTypeRepository.findByAircraftFamilyId(aircraftFamily);
		return dtoConverter.toDtos(aircraftTypes, AircraftTypeLightDto.class);
    }

    @Override
    public List<AircraftVersionLightDto> getAllAircraftVersion(Collection<Long> aircraftTypeIds) {
        List<AircraftVersion> aircraftVersions = aircraftVersionRepository.findByAircraftTypeIdIn(aircraftTypeIds);
		return dtoConverter.toDtos(aircraftVersions, AircraftVersionLightDto.class);
    }

    @Override
    public Set<Long> findInvalidAircraftTypeIds(Long aircraftFamilyId, Collection<Long> aircraftTypeIds) {
        if (aircraftFamilyId == null) {
            return aircraftTypeIds.stream().collect(Collectors.toSet());
        }
        Set<Long> validAircraftTypeIds = aircraftTypeRepository.findByAircraftFamilyId(aircraftFamilyId).stream()
                .map(AbstractBaseModel::getId).collect(Collectors.toSet());
        return aircraftTypeIds.stream().filter(e -> !validAircraftTypeIds.contains(e)).collect(Collectors.toSet());
    }

    @Override
    public Set<Long> findInvalidAircraftVersionIds(Collection<Long> aircraftTypeIds, Collection<Long> aircraftVersionIds) {
        if (aircraftTypeIds == null || aircraftTypeIds.isEmpty()) {
            return aircraftVersionIds.stream().collect(Collectors.toSet());
        }
        Set<Long> validAircraftVersionIds = aircraftVersionRepository.findByAircraftTypeIdIn(aircraftTypeIds).stream()
                .map(AbstractBaseModel::getId).collect(Collectors.toSet());
        return aircraftVersionIds.stream().filter(e -> !validAircraftVersionIds.contains(e)).collect(Collectors.toSet());
    }

}

