/*
package com.atguigu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PublicKey;
import java.util.Objects;

@SpringBootTest
public class Person {
    private String name;
    private int age;


    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }


    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }


    @Test
    public void test(){
        Person p = new Person("李华",18);
        Person p1 = new Person("李华",18);

        boolean equals = p.equals(p1);
        System.out.println("equals = " + equals);
    }
}
*/
