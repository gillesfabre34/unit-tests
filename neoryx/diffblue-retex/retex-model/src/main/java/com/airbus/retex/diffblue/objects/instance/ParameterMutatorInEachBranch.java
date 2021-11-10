package com.airbus.retex.diffblue.objects.instance;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.Person;

import static com.sun.activation.registries.LogSupport.log;

public class ParameterMutatorInEachBranch {

    public void parameterMutatorInEachBranch(Person p) {
        if (p.getCats().size() < 3) {
            p.getCats().add(new Cat());
        } else {
            p.getCats().remove(0);
            log("Too many cats !");
        }
    }
}
