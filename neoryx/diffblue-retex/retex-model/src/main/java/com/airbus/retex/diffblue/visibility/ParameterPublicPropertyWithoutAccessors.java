package com.airbus.retex.diffblue.visibility;

import com.airbus.retex.diffblue.core.Address;

public class ParameterPublicPropertyWithoutAccessors {

    public double parameterPublicPropertyWithoutAccessors(Address a) {
        if (a.street.equals("Red place")) {
            return 0;
        } else {
            return 1;
        }
    }
}
