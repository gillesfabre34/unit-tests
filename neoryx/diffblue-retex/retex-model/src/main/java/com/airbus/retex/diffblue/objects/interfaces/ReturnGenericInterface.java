package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.IAnimal;

public class ReturnGenericInterface<A extends IAnimal> {

    public int returnGenericInterface(A animal) {
        return animal.numberOfLegs();
    }
}
