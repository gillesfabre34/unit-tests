package com.airbus.retex.diffblue.objects.instance;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.Person;

import static com.sun.activation.registries.LogSupport.log;

public class ParameterMutator {

    public void parameterMutator(Person p) {
        if (p.getCats().size() < 3) {
            p.getCats().add(new Cat());
        } else {
            log("Too many cats !");
        }
    }
}
