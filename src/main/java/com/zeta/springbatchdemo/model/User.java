package com.zeta.springbatchdemo.model;

import lombok.Getter;

@Getter
public class User {
    Integer userId;
    String userName;
    String firstName;
    String lastName;
    String gender;
    String password;
    Integer status;

    public User(Integer userId, String userName, String firstName,
                String lastName, String gender, String password, Integer status) {
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.password = password;
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", password='" + password + '\'' +
                ", status=" + status +
                '}';
    }
}
