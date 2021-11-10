package com.airbus.retex.business.dto.routing;

import com.airbus.retex.business.dto.Dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSourceResolvable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoutingCreatedDto implements Dto {

    private List<Long> routingIds;

    private MessageSourceResolvable errorRoutingExists = null;

    private MessageSourceResolvable errorMappingNotFound = null;
}
