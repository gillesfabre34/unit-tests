package com.airbus.retex.diffblue.core;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Person {

    public Address address;
    public ArrayList<Cat> cats;
    public String firstName;
    public String lastName;
    private String socialNumber;
    private Boolean isHappy;
    public String role;

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.cats = new ArrayList<>();
        this.isHappy = false;
    }

}
