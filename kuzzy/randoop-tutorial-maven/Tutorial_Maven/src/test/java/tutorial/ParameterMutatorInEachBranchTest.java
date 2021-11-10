package tutorial;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParameterMutatorInEachBranchTest {
    @Test
    public void testParameterMutatorInEachBranch() {
        Person person = new Person("Jane", "Doe");
        (new ParameterMutatorInEachBranch()).parameterMutatorInEachBranch(person);
        assertEquals(1, person.getCats().size());
    }

    @Test
    public void testParameterMutatorInEachBranch2() {
        Person person = new Person("First Name", "Last Name");
        (new ParameterMutatorInEachBranch()).parameterMutatorInEachBranch(person);
        assertEquals(1, person.getCats().size());
    }
}

