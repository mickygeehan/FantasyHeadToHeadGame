package com.example.michael.fantasyheadtoheadgame.Classes;

import java.io.Serializable;

/**
 * Created by michaelgeehan on 20/03/2017.
 */

public class Game implements Serializable {
    
    private User user1,user2;

    public Game(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }
}
