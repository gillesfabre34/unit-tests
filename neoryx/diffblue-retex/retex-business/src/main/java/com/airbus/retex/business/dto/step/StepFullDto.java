package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.post.PostFullDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.step.StepFieldsEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class StepFullDto extends StepDto {
    private boolean validated;
    private Map<Language, Map<StepFieldsEnum, String>> translatedFields;
    private List<PostFullDto> posts;
    private Boolean isDeletable;

}
