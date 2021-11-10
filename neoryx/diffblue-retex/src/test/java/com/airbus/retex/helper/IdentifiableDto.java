package com.airbus.retex.helper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IdentifiableDto<T> {
    private T id;
}
