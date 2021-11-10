package com.airbus.retex.business.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostThresholdDto {
    private PostDto post;
    private Float threshold;
}
