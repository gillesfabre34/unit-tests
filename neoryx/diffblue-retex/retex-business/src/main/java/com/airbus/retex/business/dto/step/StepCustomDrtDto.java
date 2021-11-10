package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.business.dto.post.PostDrtCustomDto;
import com.airbus.retex.model.step.StepFieldsEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class StepCustomDrtDto extends StepDto {

    private final TranslationDto information = new TranslationDto(this, StepFieldsEnum.information, "");

    private final TranslationDto name = new TranslationDto(this, StepFieldsEnum.name, "");

    private StepActivationDrtDto stepActivation;

    private List<PostDrtCustomDto> posts;

}
