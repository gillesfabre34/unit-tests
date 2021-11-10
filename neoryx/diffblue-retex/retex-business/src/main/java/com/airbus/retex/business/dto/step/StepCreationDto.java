package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.VersionableInDto;
import com.airbus.retex.business.dto.post.PostCreationDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.step.StepFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepCreationDto extends VersionableInDto {
    private Map<Language, Map<StepFieldsEnum, String>> translatedFields;
    private List<String> mediaUuids;
    private List<PostCreationDto> posts;
    private StepType type;
    private Boolean isDeletable;
}
