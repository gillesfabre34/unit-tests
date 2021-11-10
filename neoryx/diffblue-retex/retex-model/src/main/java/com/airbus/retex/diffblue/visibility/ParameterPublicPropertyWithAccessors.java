package com.airbus.retex.diffblue.visibility;

import com.airbus.retex.diffblue.core.Person;

public class ParameterPublicPropertyWithAccessors {

    public double parameterPublicPropertyWithAccessors(Person a) {
        if (a.firstName.equals("Léa")) {
            return 0;
        } else {
            return 1;
        }
    }
}
