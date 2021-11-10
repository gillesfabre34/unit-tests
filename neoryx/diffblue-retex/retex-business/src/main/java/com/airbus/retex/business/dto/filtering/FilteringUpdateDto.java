package com.airbus.retex.business.dto.filtering;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.user.ReferenceDto;
import com.airbus.retex.business.util.ConstantRegex;
import com.airbus.retex.model.common.EnumFilteringPosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class FilteringUpdateDto implements Dto {

    private ReferenceDto<Long> aircraftFamily;

    private ReferenceDto<Long> aircraftType;

    private ReferenceDto<Long> aircraftVersion;

    @Pattern(regexp = ConstantRegex.REGEX_NUMBER_NULLABLE)
    private String aircraftSerialNumber;

    private EnumFilteringPosition position;

    @Pattern(regexp = ConstantRegex.REGEX_ALPHA_NUMBER_NULLABLE)
    private String notification;

    private List<UUID> medias = new ArrayList<>();
}
