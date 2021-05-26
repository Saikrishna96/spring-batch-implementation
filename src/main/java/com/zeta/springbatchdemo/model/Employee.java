package com.zeta.springbatchdemo.model;

import lombok.Data;
import lombok.Getter;

@Getter
public class Employee {
    private Integer id;
    private String emailAddress;
    private String firstName;
    private String lastName;

    public Employee(Integer id, String emailAddress, String firstName, String lastName) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", emailAddress='" + emailAddress + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
