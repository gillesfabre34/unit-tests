package com.airbus.retex.business.dto.drt;

import com.airbus.retex.business.dto.common.PaginationDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class DrtFilteringDto extends PaginationDto {
    private String partNumber;
    private String serialNumber;
}