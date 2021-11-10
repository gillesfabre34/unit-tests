package com.airbus.retex.business.mapper;

import com.airbus.retex.model.AbstractTranslation;
import com.airbus.retex.model.basic.IIdentifiedVersionModel;
import org.mapstruct.*;

/**
 * Abstract class for versionable entity cloning
 */
public abstract class AbstractCloner {

    @AfterMapping
    public <T extends IIdentifiedVersionModel> void setNullId(@MappingTarget T clone) {

        clone.setTechnicalId(null);

    }

    @AfterMapping
    public <T extends AbstractTranslation> void setTranslationNullId(@MappingTarget T clone) {

        clone.setId(null);

    }

}
