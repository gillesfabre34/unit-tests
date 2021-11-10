package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.util.ConstantRegex;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class PartCreationDto extends PartUpdateHeaderDto {

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER_NULLABLE)
    private String partNumber;

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER_NULLABLE)
    private String partNumberRoot;
}
