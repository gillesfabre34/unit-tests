package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.IAnimal;

public class FieldTypedWithGenericInterface<A extends IAnimal> {

    public A animal;

    public int fieldTypedWithGenericInterface() {
        return animal.numberOfLegs();
    }
}
