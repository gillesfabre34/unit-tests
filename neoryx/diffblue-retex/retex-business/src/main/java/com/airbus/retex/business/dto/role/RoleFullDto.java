package com.airbus.retex.business.dto.role;

import com.airbus.retex.model.common.Language;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RoleFullDto extends RoleDto {

    private Map<Language, String> translatedLabels;
}
