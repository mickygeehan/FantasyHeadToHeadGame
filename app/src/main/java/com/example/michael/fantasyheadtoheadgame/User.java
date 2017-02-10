package com.example.michael.fantasyheadtoheadgame;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class User {
    private String username,email;
    private int id;

    public User(String username, String email, int id) {
        this.username = username;
        this.email = email;
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
