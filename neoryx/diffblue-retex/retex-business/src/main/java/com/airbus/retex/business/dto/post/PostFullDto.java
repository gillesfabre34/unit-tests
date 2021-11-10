package com.airbus.retex.business.dto.post;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.post.PostFieldsEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Map;

@Getter
@Setter
public class PostFullDto extends PostDto{

    @Nullable
    private Map<Language, Map<PostFieldsEnum, String>> translatedFields;
    
    private Boolean isDeletable;
}
