package com.airbus.retex.business.dto.drt;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.origin.OriginLightDto;
import com.airbus.retex.business.dto.part.PartDesignationDto;
import com.airbus.retex.business.dto.user.UserLightDto;
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
public class DrtHeaderDto implements Dto {

    private Long routingId;

    private Long routingVersion;

    private String associatedRequest;

    private Long id;

    @NotNull
    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String partNumber;

    @NotNull
    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER)
    private String serialNumber;

    private PartDesignationDto designation;

    private LocalDate integrationDate;

    private EnumStatus status;

    private OriginLightDto origin;

    private UserLightDto assignedOperator;
}
