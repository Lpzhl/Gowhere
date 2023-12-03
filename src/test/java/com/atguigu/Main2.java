package com.atguigu;

import java.util.Arrays;

class Person implements Comparable<Person> {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public int compareTo(Person other) {
        return this.age - other.age;
    }


    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Person{" + "name='" + name + '\'' + ", age=" + age + '}';
    }
}



public class Main2 {
    public static void main(String[] args) {
        Person[] people = {
                new Person("廖梓行", 70),
                new Person("张培灵", 25),
                new Person("李", 35)
        };

        Arrays.sort(people);

        for (Person person : people) {
            System.out.println(person);
        }
    }
}