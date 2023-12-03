package com.atguigu;

import java.util.Arrays;

public class Main1 {
    public static void main(String[] args) {
        Person[] people = {
                new Person("Alice", 30),
                new Person("Bob", 25),
                new Person("Charlie", 35)
        };

        Arrays.sort(people);

        for (Person person : people) {
            System.out.println(person);
        }
    }
}
