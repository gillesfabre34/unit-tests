package com.airbus.retex.business.dto.step;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.post.PostCustomThingworxDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StepCustomThingworxDto implements Dto {
    private UUID mediaUuid;
    private List<PostCustomThingworxDto> posts;
}
