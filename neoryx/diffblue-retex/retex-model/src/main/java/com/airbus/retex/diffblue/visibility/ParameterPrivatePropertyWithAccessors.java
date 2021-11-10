package com.airbus.retex.diffblue.visibility;

import com.airbus.retex.diffblue.core.Person;

public class ParameterPrivatePropertyWithAccessors {

    public double parameterPrivatePropertyWithAccessors(Person a) {
        if (a.getSocialNumber().equals("1432355332")) {
            return 0;
        } else {
            return 1;
        }
    }
}
