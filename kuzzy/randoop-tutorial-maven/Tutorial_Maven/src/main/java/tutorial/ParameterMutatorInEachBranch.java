package tutorial;

import static com.sun.activation.registries.LogSupport.log;

public class ParameterMutatorInEachBranch {

    public void parameterMutatorInEachBranch(Person p) {
        if (p.getCats().size() < 3) {
            p.getCats().add(new Cat("Biela", 7));
        } else {
            p.getCats().remove(0);
            log("Too many cats !");
        }
    }
}
