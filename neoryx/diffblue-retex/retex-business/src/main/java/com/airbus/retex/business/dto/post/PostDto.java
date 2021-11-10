package com.airbus.retex.business.dto.post;

import com.airbus.retex.business.dto.ITranslationDto;
import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.business.dto.VersionableOutDto;
import com.airbus.retex.business.dto.measureUnit.MeasureUnitTranslatedDto;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.post.PostTranslation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PostDto extends VersionableOutDto implements ITranslationDto<PostTranslation, PostFieldsEnum> {
    @JsonIgnore
    private Long technicalId;


    private final TranslationDto designation = new TranslationDto(this, PostFieldsEnum.designation);

    private MeasureUnitTranslatedDto measureUnit;
    private Long measureUnitId;

    @Override
    public Class<PostTranslation> getClassTranslation() {
        return PostTranslation.class;
    }

    @Override
    public Long getEntityId() {
        return technicalId;
    }
}
