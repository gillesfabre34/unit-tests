package tutorial;


import java.util.ArrayList;

public class Person {

    public Address address;
    public ArrayList<Cat> cats;
    public String firstName;
    public String lastName;
    private String socialNumber;
    private Boolean isHappy;
    public String role;


    public ArrayList<Cat> getCats() {
        return cats;
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cats = new ArrayList<Cat>();
        this.isHappy = false;
    }

}
