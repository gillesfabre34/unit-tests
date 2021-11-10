package com.airbus.retex.business.dto.damage;

import com.airbus.retex.business.dto.*;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import com.airbus.retex.model.damage.DamageTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageNameDto extends VersionableOutDto implements ITranslationDto<DamageTranslation, DamageFieldsEnum> {

    @JsonIgnore
    private Long technicalId;

    private final TranslationDto name = new TranslationDto(this, DamageFieldsEnum.name);

    @Override
    public Class<DamageTranslation> getClassTranslation() {
        return DamageTranslation.class;
    }

    @Override
    public Long getEntityId() {
        return technicalId;
    }
}
