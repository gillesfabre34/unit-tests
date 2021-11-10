package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.dto.airbusentity.AirbusEntityLightDto;
import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto extends RequestLightDto {

    private Long versionNumber;
    private EnumStatus status;
    private UserLightDto requester ;
    private AirbusEntityLightDto airbusEntity;
    private OriginDto origin ;
}
