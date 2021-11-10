package com.airbus.retex.diffblue.objects.ifs;

import com.airbus.retex.diffblue.core.Person;

public class IfObjectEquality {

    public double ifObjectEquality(Person a) {
        Person b = new Person("Léa", "Renoir");
        if (a.equals(b)) {
            return 0;
        } else {
            return 1;
        }
    }
}
