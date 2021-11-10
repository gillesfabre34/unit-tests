package com.airbus.retex.service.impl.mission;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import com.airbus.retex.persistence.mission.MissionTypeRepository;
import com.airbus.retex.service.mission.IMissionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MissionTypeServiceImpl implements IMissionTypeService {

    @Autowired
    private MissionTypeRepository missionTypeRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<MissionTypeLightDto> getAllMissionsType() {
        return dtoConverter.convert(missionTypeRepository.findAll(), MissionTypeLightDto::new);
    }
}
