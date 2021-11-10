package com.airbus.retex.routingComponent;

import lombok.Data;

@Data
public class RoutingComponentHelperDto {
    private Long routingComponentIndexId;
    private Long versionNumber;
    private Boolean isLatestVersion;
}
