package com.airbus.retex.service.mission;

import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface IMissionTypeService {
    /**
     * Get all missions type
     *
     * @return
     */
    List<MissionTypeLightDto> getAllMissionsType();
}
