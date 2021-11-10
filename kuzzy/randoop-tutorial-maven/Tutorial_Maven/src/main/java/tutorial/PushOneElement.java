package tutorial;

import static com.sun.activation.registries.LogSupport.log;

public class PushOneElement {

    public int pushOneElement(Person p) {
        if (p.getCats().size() < 3) {
            p.getCats().add(new Cat("Biela", 7));
            return 2;
        } else {
            log("Too many cats !");
            return 3;
        }
    }
}
