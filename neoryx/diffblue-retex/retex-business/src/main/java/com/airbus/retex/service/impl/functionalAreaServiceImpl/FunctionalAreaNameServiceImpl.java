package com.airbus.retex.service.impl.functionalAreaServiceImpl;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaNameRepository;
import com.airbus.retex.service.functionalArea.IFunctionalAreaNameService;
import com.airbus.retex.service.translate.ITranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class FunctionalAreaNameServiceImpl implements IFunctionalAreaNameService {
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private FunctionalAreaNameRepository functionalAreaNameRepository;
    @Autowired
    private ITranslateService iTranslateService;

    @Override
    public List<FunctionalAreaNameDto> getAllFunctionalAreasNames(Language language) {
        return dtoConverter.convert( functionalAreaNameRepository.findAll(), FunctionalAreaNameDto::new);
    }
}
