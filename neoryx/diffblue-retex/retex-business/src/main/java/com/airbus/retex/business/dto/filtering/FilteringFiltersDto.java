package com.airbus.retex.business.dto.filtering;

import com.airbus.retex.business.dto.common.PaginationDto;
import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.business.dto.part.PartDesignationLightDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class FilteringFiltersDto extends PaginationDto {

    private Long drtId;

    private String partNumber;

    private String serialNumber;

    private String equipmentNumber;

    private PartDesignationLightDto designation;

    private LocalDate integrationDate;

    private EnumStatus status;

    private OriginDto origin;
}
