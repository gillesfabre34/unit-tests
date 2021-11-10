package com.airbus.retex.diffblue.objects.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Person;
import org.junit.jupiter.api.Test;

public class IfObjectEqualityTest {
    @Test
    public void testIfPublicPropertyEquality() {
        Person a = new Person("Léa", "lastName");
        assertEquals(1.0, (new IfObjectEquality()).ifObjectEquality(a));
    }

    @Test
    public void testIfPublicPropertyEquality2() {
        Person a = new Person("Jane", "Doe");
        assertEquals(1.0, (new IfObjectEquality()).ifObjectEquality(a));
    }
}

