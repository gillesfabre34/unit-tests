package com.airbus.retex.diffblue.primitives.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Person;
import com.airbus.retex.diffblue.visibility.ParameterPublicPropertyWithAccessors;
import org.junit.jupiter.api.Test;

public class ParameterPublicPropertyWithAccessorsTest {
    @Test
    public void testIfFirstNameEquality() {
        Person a = new Person("Léa", "Doe");
        assertEquals(0.0, (new ParameterPublicPropertyWithAccessors()).parameterPublicPropertyWithAccessors(a));
    }

    @Test
    public void testIfFirstNameEquality2() {
        Person a = new Person("Jane", "Doe");
        assertEquals(1.0, (new ParameterPublicPropertyWithAccessors()).parameterPublicPropertyWithAccessors(a));
    }

    @Test
    public void testIfFirstNameEquality3() {
        Person a = new Person("Léa", "Léa");
        assertEquals(0.0, (new ParameterPublicPropertyWithAccessors()).parameterPublicPropertyWithAccessors(a));
    }
}

