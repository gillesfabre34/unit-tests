package com.airbus.retex.business.dto.request;


@SuppressWarnings("all")
public enum RequestSortingValues {
    name("translates.value"),
    reference("reference"),
    airbus_entity("airbusEntity.countryName"),
    user("requester.firstName"),
    creation_date("creationDate"),
    origin("origin.name"),
    status("status"),
    ata_code("ataCode");

    public final String label;

    RequestSortingValues(String label) {
        this.label = label;
    }
}
