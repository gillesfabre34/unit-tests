package com.airbus.retex.persistence.functionalArea;

public class FunctionalAreaRepositoryConstant {

    /***
     * private contructor
     */
    private FunctionalAreaRepositoryConstant() {

    }

    static final String QUERY_FIND_BY_ROUTING = "SELECT fa FROM FunctionalArea fa " + "JOIN Part p ON p = fa.part "
            + "JOIN Routing ro ON ro.part = p.id " + "WHERE ro.id = :routingId ";
}
