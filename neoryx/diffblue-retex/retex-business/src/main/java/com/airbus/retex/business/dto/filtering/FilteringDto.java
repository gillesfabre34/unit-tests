package com.airbus.retex.business.dto.filtering;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.business.dto.part.PartDesignationDto;
import com.airbus.retex.business.util.ConstantRegex;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FilteringDto implements Dto {

    private Long id;

    private Long drtId;

    @NotNull
    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String partNumber;

    @NotNull
    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String serialNumber;

    @NotNull
    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String equipmentNumber;

    private PartDesignationDto designation;

    private LocalDate integrationDate;

    private EnumStatus status;

    private OriginDto origin;
}
