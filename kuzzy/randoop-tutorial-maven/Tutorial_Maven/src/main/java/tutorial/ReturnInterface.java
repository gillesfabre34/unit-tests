package tutorial;


public class ReturnInterface {

    public IAnimal returnInterface() {
        Cat cat = new Cat("Biela", 7);
        cat.setName("Biela");
        return cat;
    }
}
