package com.airbus.retex.diffblue.objects.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Cat;
import com.airbus.retex.diffblue.core.IAnimal;
import com.airbus.retex.diffblue.objects.interfaces.ReturnGenericInterface;
import org.junit.jupiter.api.Test;

public class ReturnGenericInterfaceTest {
    @Test
    public void testReturnGenericInterface() {
        ReturnGenericInterface<IAnimal> returnGenericInterface = new ReturnGenericInterface<IAnimal>();
        assertEquals(4, returnGenericInterface.returnGenericInterface(new Cat()));
    }

    @Test
    public void testReturnGenericInterface2() {
        Cat cat = new Cat();
        cat.setName("Name");
        assertEquals(4, (new ReturnGenericInterface<IAnimal>()).returnGenericInterface(cat));
    }
}

