package com.airbus.retex.business.dto.aircraft;

import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AircraftTypeIdListDto implements Dto {
    @NotNull
    private List<Long> aircraftTypeIds;
}
