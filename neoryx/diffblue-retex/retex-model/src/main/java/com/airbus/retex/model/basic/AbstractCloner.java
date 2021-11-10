package com.airbus.retex.model.basic;

import com.airbus.retex.model.AbstractTranslation;
import lombok.Getter;
import lombok.Setter;
import org.mapstruct.*;

/**
 * Abstract class for versionable entity cloning
 */
@Setter
@Getter
public abstract class AbstractCloner {

    @AfterMapping
    public <T extends IIdentifiedVersionModel> void setNullId(@MappingTarget T clone) {
        if(null != clone){
            clone.setTechnicalId(null);
        }

    }

    @AfterMapping
    public <T extends AbstractTranslation> void setNullId(@MappingTarget T clone) {
        if(null != clone){
            clone.setId(null);
        }

    }

}
