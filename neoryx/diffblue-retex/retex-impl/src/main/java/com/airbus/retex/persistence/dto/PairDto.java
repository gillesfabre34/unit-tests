package com.airbus.retex.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PairDto<F, S> {
    private F first;
    private S second;
}
