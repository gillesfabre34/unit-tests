package com.airbus.retex.business.dto.common;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class PaginationDto {

    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 10;
}
