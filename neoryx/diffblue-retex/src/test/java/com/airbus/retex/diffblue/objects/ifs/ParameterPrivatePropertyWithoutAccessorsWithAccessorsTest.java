package com.airbus.retex.diffblue.objects.ifs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.airbus.retex.diffblue.core.Person;
import com.airbus.retex.diffblue.visibility.ParameterPrivatePropertyWithAccessors;
import org.junit.jupiter.api.Test;

public class ParameterPrivatePropertyWithoutAccessorsWithAccessorsTest {
    @Test
    public void testIfPrivatePropertyEquality() {
        Person person = new Person("Jane", "Doe");
        person.setSocialNumber("1432355332");
        assertEquals(0.0, (new ParameterPrivatePropertyWithAccessors()).parameterPrivatePropertyWithAccessors(person));
    }

    @Test
    public void testIfPrivatePropertyEquality2() {
        Person person = new Person("Jane", "Doe");
        person.setSocialNumber("socialNumber");
        assertEquals(1.0, (new ParameterPrivatePropertyWithAccessors()).parameterPrivatePropertyWithAccessors(person));
    }
}

