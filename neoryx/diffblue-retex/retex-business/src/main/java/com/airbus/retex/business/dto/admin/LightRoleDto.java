package com.airbus.retex.business.dto.admin;

import com.airbus.retex.business.dto.Dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LightRoleDto implements Dto {

    private Long id;
    private String isoCode;
    private String label;
}
