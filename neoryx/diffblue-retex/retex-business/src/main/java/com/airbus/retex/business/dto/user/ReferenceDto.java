package com.airbus.retex.business.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceDto<T extends Object> implements IIdentifiableDto<T> {
    private T id;
}
