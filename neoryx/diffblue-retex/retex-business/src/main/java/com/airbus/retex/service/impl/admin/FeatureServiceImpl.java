package com.airbus.retex.service.impl.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.feature.FeatureDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.service.admin.IFeatureService;

@Service
@Transactional(rollbackFor = Exception.class)
public class FeatureServiceImpl implements IFeatureService {


    @Autowired
    private MessageSource messageSource;

    @Override
    public List<FeatureDto> getAllFeatures() {
        List<FeatureDto> featureDtos = new ArrayList<>();
        for(var feature : FeatureCode.values()) {
            StringBuilder code = new StringBuilder("retex.feature.").append(feature.name()).append(".label");
            String label = messageSource.getMessage(code.toString(), null, LocaleContextHolder.getLocale());//FIXME avoid using LocalContextHolder (controller layer)
			featureDtos.add(new FeatureDto(feature.name(), label));
        }
        return featureDtos;
    }
}
