package com.airbus.retex.diffblue.objects.instance;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.IAnimal;

public class MutateField {

    public Cat cat = new Cat();

    public void mutateField() {
        cat.setName("Biela");
    }
}
