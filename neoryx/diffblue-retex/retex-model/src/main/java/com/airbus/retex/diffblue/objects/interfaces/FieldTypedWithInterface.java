package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.IAnimal;

public class FieldTypedWithInterface {

    public IAnimal animal;

    public int fieldTypedWithInterface() {
        return animal.numberOfLegs();
    }
}
