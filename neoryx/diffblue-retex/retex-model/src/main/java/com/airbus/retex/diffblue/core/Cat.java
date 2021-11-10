package com.airbus.retex.diffblue.core;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cat implements IAnimal {

    public String name;
    public int age;


    @Override
    public int numberOfLegs() {
        return 4;
    }
}
