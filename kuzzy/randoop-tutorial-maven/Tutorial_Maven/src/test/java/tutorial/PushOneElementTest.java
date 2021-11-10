package tutorial;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PushOneElementTest {
    @Test
    public void testPushOneElement() {
        Person person = new Person("Jane", "Doe");
        assertEquals(2, (new PushOneElement()).pushOneElement(person));
        assertEquals(1, person.getCats().size());
    }

    @Test
    public void testPushOneElement2() {
        Person person = new Person("First Name", "Last Name");
        assertEquals(2, (new PushOneElement()).pushOneElement(person));
        assertEquals(1, person.getCats().size());
    }
}

