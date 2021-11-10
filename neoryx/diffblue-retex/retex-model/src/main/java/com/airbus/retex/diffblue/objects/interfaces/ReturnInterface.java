package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.IAnimal;
import com.airbus.retex.diffblue.core.Person;

import static com.sun.activation.registries.LogSupport.log;

public class ReturnInterface {

    public IAnimal returnInterface() {
        Cat cat = new Cat();
        cat.setName("Biela");
        return cat;
    }
}
