package com.airbus.retex.business.dto.childRequest;


import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.service.impl.util.ChildRequestUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildRequestDetailDto implements Dto {
    @NotNull
    private Long partId;

    private Long aircraftFamilyId;

    @NotNull
    private List<Long> aircraftTypeIds;

    @NotNull
    private List<Long> aircraftVersionIds;

    @NotNull
    private Long missionTypeId;

    @NotNull
    private Long environmentId;

    @NotNull
    private List<Long> clientIds;

    @NotNull
    @PositiveOrZero(message = "Drt to inspect must contain a positive value")
    private Integer drtToInspect;

    private List<UUID> medias;

    private List<@Pattern(regexp = ChildRequestUtil.SERIAL_NUMBER_REGEX, message = "Serial Number must contain only alphanumeric values")String> serialNumbers;

    @NotNull
    private EnumStatus status;

    private Boolean isDeletable;
}
