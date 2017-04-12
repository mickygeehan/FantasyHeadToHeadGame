package com.example.michael.fantasyheadtoheadgame.Classes;

import java.io.Serializable;

/**
 * Created by michaelgeehan on 14/02/2017.
 */

public class Player implements Serializable {
    int posInTeam;
    String firstName,secondName,webName;
    int teamCode,id,playerPosition,imageCode;
    double cost;

    public Player(int posInTeam, String firstName, String secondName, String webName, int teamCode, int id, int playerPosition, double cost,int imageCode) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.webName = webName;
        this.teamCode = teamCode;
        this.id = id;
        this.playerPosition = playerPosition;
        this.cost = cost;
        this.posInTeam = posInTeam;
        this.imageCode = imageCode;
    }
    


    public int getImageCode() {
        return imageCode;
    }

    public void setImageCode(int imageCode) {
        this.imageCode = imageCode;
    }

    public int getPosInTeam() {
        return posInTeam;
    }

    public void setPosInTeam(int posInTeam) {
        this.posInTeam = posInTeam;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getWebName() {
        return webName;
    }

    public void setWebName(String webName) {
        this.webName = webName;
    }

    public int getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(int teamCode) {
        this.teamCode = teamCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerPosition() {
        return playerPosition;
    }

    public void setPlayerPosition(int playerPosition) {
        this.playerPosition = playerPosition;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
