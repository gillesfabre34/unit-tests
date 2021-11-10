package com.airbus.retex.service.impl.thingworx.internal;

import java.util.List;

import com.airbus.retex.business.dto.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepUrlDto implements Dto {
    private Long id;
    private String name;
    private List<PostUrlDto> posts;
}
