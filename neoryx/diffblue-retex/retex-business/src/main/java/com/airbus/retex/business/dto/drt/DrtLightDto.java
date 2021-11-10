package com.airbus.retex.business.dto.drt;

import com.airbus.retex.business.dto.LightDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DrtLightDto extends LightDto {



    private LocalDate integrationDate;

    private EnumStatus status;

    //TODO: add missing fields (to be done as part of DRT stories)
}
