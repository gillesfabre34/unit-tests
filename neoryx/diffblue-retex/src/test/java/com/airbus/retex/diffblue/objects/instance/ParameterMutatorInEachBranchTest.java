package com.airbus.retex.diffblue.objects.instance;

import com.airbus.retex.diffblue.core.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterMutatorInEachBranchTest {

    private ParameterMutatorInEachBranch parameterMutatorInEachBranchUnderTest;

    @BeforeEach
    void setUp() {
        parameterMutatorInEachBranchUnderTest = new ParameterMutatorInEachBranch();
    }

    @Test
    void testParameterMutatorInEachBranch() {
        // Setup
        final Person p = new Person(null, null);

        // Run the test
        parameterMutatorInEachBranchUnderTest.parameterMutatorInEachBranch(p);

        // Verify the results
    }
}
