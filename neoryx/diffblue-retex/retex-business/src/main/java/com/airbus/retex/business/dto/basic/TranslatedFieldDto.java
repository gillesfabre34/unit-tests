package com.airbus.retex.business.dto.basic;

import com.airbus.retex.model.common.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class TranslatedFieldDto<T> {
    // T is the enum name of label, ex: DamageFiels
    @ApiModelProperty
    private T label;

    private Map<Language, String> values;
}
