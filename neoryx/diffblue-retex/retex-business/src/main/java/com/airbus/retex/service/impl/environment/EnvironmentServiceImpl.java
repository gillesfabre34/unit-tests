package com.airbus.retex.service.impl.environment;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import com.airbus.retex.persistence.environment.EnvironmentRepository;
import com.airbus.retex.service.environment.IEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EnvironmentServiceImpl implements IEnvironmentService {

    @Autowired
    private EnvironmentRepository environmentRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<EnvironmentLightDto> getAllEnvironments() {
        return dtoConverter.convert(environmentRepository.findAll(), EnvironmentLightDto::new);
    }


}
