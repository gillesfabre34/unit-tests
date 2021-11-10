package com.airbus.retex.service.environment;

import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IEnvironmentService {
    /**
     * Get all environments
     *
     * @return
     */
    List<EnvironmentLightDto> getAllEnvironments();
}
