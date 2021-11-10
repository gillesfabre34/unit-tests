package tutorial;

public class Cat implements IAnimal {

    public String name;
    public int age;

    public Cat(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int numberOfLegs() {
        return 4;
    }
}
