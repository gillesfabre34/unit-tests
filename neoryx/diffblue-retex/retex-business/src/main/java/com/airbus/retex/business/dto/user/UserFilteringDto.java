package com.airbus.retex.business.dto.user;

import com.airbus.retex.business.dto.common.PaginationDto;
import com.airbus.retex.model.admin.role.RoleCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class UserFilteringDto extends PaginationDto {

    private Long airbusEntityId;
    private String search = "";        // defaultValue= "" in old fashion @RequestParam
    private Boolean onlyActive = true; // defaultValue= true in old fashion @RequestParam
    private RoleCode roleCode;
}
