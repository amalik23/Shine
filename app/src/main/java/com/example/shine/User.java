package com.example.shine;

public class User {
    // Can add more stuff for each user to save their settings and stuff
    private String name, email;

    User () {}

    User (String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
