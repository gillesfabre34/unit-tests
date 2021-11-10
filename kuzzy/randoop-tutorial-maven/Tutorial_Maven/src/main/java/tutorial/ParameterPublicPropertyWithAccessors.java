package tutorial;

public class ParameterPublicPropertyWithAccessors {

    public double parameterPublicPropertyWithAccessors(Person a) {
        if (a.firstName.equals("Léa")) {
            return 0;
        } else {
            return 1;
        }
    }
}
