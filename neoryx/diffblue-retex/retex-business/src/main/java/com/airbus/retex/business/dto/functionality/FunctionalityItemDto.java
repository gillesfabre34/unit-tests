package com.airbus.retex.business.dto.functionality;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FunctionalityItemDto {
    private Long id;
    private String name;
    private boolean deletable;
}
