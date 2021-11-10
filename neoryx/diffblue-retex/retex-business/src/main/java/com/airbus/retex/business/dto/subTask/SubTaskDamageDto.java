package com.airbus.retex.business.dto.subTask;

import com.airbus.retex.business.dto.ITranslationDto;
import com.airbus.retex.business.dto.TranslationDto;

import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import com.airbus.retex.model.damage.DamageTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubTaskDamageDto extends VersionableOutDto implements ITranslationDto<DamageTranslation, DamageFieldsEnum> {

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
