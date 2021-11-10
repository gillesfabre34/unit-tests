package com.airbus.retex.diffblue.objects.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Person;
import org.junit.jupiter.api.Test;

public class PushOneElementTest {
    @Test
    public void testPushOneElement() {
        Person person = new Person("Jane", "Doe");
        (new PushOneElement()).pushOneElement(person);
        assertEquals(1, person.getCats().size());
    }

    @Test
    public void testPushOneElement2() {
        Person person = new Person("Jane", "Doe");
        person.setIsHappy(true);
        (new PushOneElement()).pushOneElement(person);
        assertEquals(1, person.getCats().size());
    }
}

