package com.airbus.retex.business.dto.post;

import com.airbus.retex.business.dto.VersionableInDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.post.PostFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCreationDto extends VersionableInDto {

    private Map<Language, Map<PostFieldsEnum, String>> translatedFields;
    private Long measureUnitId;
    private Boolean isDeletable;
}
