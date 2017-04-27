package com.example.michael.fantasyheadtoheadgame.Classes;

import java.io.Serializable;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class User implements Serializable {
    private String username,email;
    private int id,points;
    private double budget;

    public User(String username, String email, int id, double budget) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.budget = budget;
    }
    public User(String username, String email, int id) {
        this.username = username;
        this.email = email;
        this.id = id;

    }
    public User(String username, String email, int id, double budget,int points) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.budget = budget;
        this.points = points;
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

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget; 
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

}
