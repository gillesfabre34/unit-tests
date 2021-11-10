package com.airbus.retex.diffblue.objects.instance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.airbus.retex.diffblue.core.Person;
import org.junit.jupiter.api.Test;

public class PushManyElementsTest {
    @Test
    public void testPushManyElements() {
        Person person = new Person("Jane", "Doe");
        (new PushManyElements()).pushManyElements(person, 10);
        assertEquals(10, person.getCats().size());
        assertTrue(person.getIsHappy());
    }

    @Test
    public void testPushManyElements2() {
        Person person = new Person("Jane", "Doe");
        (new PushManyElements()).pushManyElements(person, 0);
        assertFalse(person.getIsHappy());
    }

    @Test
    public void testPushManyElements3() {
        Person person = new Person("Jane", "Doe");
        person.setLastName("Doe");
        (new PushManyElements()).pushManyElements(person, 0);
        assertFalse(person.getIsHappy());
    }
}

