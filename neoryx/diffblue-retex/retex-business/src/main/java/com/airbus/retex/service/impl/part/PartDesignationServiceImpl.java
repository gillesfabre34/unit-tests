package com.airbus.retex.service.impl.part;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.part.PartDesignationLightDto;
import com.airbus.retex.business.dto.part.PartFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.part.PartDesignation;
import com.airbus.retex.persistence.part.PartDesignationRepository;
import com.airbus.retex.service.part.IPartDesignationService;
import com.airbus.retex.service.translate.ITranslateService;

@Service
@Transactional(rollbackFor = Exception.class)
public class PartDesignationServiceImpl implements IPartDesignationService {

    @Autowired
    private PartDesignationRepository partDesignationRepository;


    @Autowired
    private ITranslateService iTranslateService;


    @Override
    public List<PartDesignationLightDto> getAllPartsDesignation(Language language) {
        List<PartDesignation> partDesignations = partDesignationRepository.findAll();
        List<PartDesignationLightDto> result = new ArrayList<>();

        for(var partDesignation : partDesignations) {
            PartDesignationLightDto partDesignationLightDto=new PartDesignationLightDto();
            partDesignationLightDto.setId(partDesignation.getId());

            // get designation according to given language
            String designationValue = iTranslateService.getFieldValue(partDesignation.getClass().getSimpleName(), partDesignation.getId(), PartFieldsEnum.designation.name(), language);
            partDesignationLightDto.setDesignation(designationValue);
            result.add(partDesignationLightDto);
        }
        return result;
    }
}
