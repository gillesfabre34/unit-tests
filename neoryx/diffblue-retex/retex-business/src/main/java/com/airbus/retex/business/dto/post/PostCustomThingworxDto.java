package com.airbus.retex.business.dto.post;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCustomThingworxDto implements Dto {
    private Long postId;
    private Float controlValue;
    private String unitName;
}
