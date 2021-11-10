package com.airbus.retex.service.impl.functionality;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.functionality.FunctionalityLightDto;
import com.airbus.retex.persistence.functionality.FunctionalityRepository;
import com.airbus.retex.service.functionality.IFunctionalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FunctionalityServiceImpl implements IFunctionalityService {
    @Autowired
    private FunctionalityRepository functionalityRepository;

    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<FunctionalityLightDto> getFunctionalities() {
        return dtoConverter.convert(functionalityRepository.findAll(), FunctionalityLightDto::new);
    }
}
