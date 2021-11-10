package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLightDto implements Dto, IIdentifiableDto<Long> {

    private Long id;

    private String firstName;

    private String lastName;
}
