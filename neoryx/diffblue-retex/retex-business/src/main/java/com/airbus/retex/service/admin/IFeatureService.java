package com.airbus.retex.service.admin;

import com.airbus.retex.business.dto.feature.FeatureDto;

import java.util.List;

public interface IFeatureService {

    /**
     * @return find all features with all labels associated
     */
    List<FeatureDto> getAllFeatures();
}
