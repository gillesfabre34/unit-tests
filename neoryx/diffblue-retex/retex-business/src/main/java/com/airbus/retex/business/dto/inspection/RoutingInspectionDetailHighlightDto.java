package com.airbus.retex.business.dto.inspection;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexNameDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoutingInspectionDetailHighlightDto implements Dto {

    private List<RoutingInspectionDetailsDto> inspectionDetail;

    private List<RoutingComponentIndexNameDto> newRoutingComponentIndex;

}

