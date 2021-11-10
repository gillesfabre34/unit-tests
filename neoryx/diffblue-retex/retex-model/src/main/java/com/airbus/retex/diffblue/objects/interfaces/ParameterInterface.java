package com.airbus.retex.diffblue.objects.interfaces;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.IAnimal;

public class ParameterInterface {

    public int parameterInterface(IAnimal cat) {
        return cat.numberOfLegs();
    }
}
