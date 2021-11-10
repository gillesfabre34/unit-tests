package com.airbus.retex.business.dto.role;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumActiveState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleLightDto extends TranslatedDto {
    private Long id;

    private RoleCode roleCode;

    private EnumActiveState state;

    private  final TranslateDto label = new TranslateDto(this, RoleFieldEnum.label);

    @Override
    public String getClassName() {
        return Role.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
