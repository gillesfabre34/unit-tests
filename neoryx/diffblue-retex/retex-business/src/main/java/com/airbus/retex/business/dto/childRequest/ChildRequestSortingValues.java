package com.airbus.retex.business.dto.childRequest;

@SuppressWarnings("all")
public enum ChildRequestSortingValues {
    id("id"),
    creation_date("creationDate"),
    status("status");

    public final String label;

    ChildRequestSortingValues(String label) {
        this.label = label;
    }
}
