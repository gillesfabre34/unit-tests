package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.common.PaginationDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PartFilteringDto extends PaginationDto {

    private String ataCode;
    private String partNumber;
    private Long designationId;
}
