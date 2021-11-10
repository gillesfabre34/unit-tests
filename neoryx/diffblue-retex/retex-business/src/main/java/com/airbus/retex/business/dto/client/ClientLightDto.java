package com.airbus.retex.business.dto.client;

import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientLightDto implements Dto {
    private Long id;

    private String name;

}
