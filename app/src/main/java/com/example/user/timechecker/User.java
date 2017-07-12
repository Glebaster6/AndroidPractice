package com.example.user.timechecker;

public class User {
    private String secondName;
    private String firstName;
    private String fatherName;

    public User(String secondName, String firstName, String fatherName) {
        this.secondName = secondName;
        this.firstName = firstName;
        this.fatherName = fatherName;
    }


    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }
}