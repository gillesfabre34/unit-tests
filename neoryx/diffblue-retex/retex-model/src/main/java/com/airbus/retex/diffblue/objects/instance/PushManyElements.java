package com.airbus.retex.diffblue.objects.instance;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.Person;

import static com.sun.activation.registries.LogSupport.log;

public class PushManyElements {

    public void pushManyElements(Person p, int numberOfCats) {
        for (int i = 0; i < numberOfCats; i++) {
            p.getCats().add(new Cat());
        }
        if (p.getCats().size() > 0) {
            p.setIsHappy(true);
        } else {
            p.setIsHappy(false);
        }
    }
}
