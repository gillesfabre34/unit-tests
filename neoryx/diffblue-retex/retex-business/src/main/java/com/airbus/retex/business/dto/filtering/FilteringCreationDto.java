package com.airbus.retex.business.dto.filtering;

import com.airbus.retex.business.dto.CreationDto;
import com.airbus.retex.business.util.ConstantRegex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class FilteringCreationDto implements CreationDto {

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String partNumber;

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String serialNumber;

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String equipmentNumber;
}
