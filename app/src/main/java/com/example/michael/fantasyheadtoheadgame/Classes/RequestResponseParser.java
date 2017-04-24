package com.example.michael.fantasyheadtoheadgame.Classes;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.michael.fantasyheadtoheadgame.ActivityScreens.UserTeamScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by michaelgeehan on 03/04/2017.
 */

public class RequestResponseParser implements Serializable{
    
    
    
    public RequestResponseParser(){
        super();
    }
    
    public User parseLoginResponse(String response){
        User userObj = null;
        JSONObject obj = null;
        try {
            System.out.println(response);
            obj = new JSONObject(response);
            JSONArray geodata = obj.getJSONArray("Users");
            int n = geodata.length();

            for (int i = 0; i < n; ++i) {
                final JSONObject user = geodata.getJSONObject(i);
                userObj = new User(user.getString("username"),user.getString("email"),user.getInt("ID"),user.getInt("budget"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userObj;
    }
    
    public ArrayList<Player> parseUserTeamResponse(String response){
        ArrayList<Player> players = new ArrayList<Player>();
        Player playerObj = null;
        JSONObject obj = null;
        try {
            obj = new JSONObject(response);
            JSONArray geodata = obj.getJSONArray("results");
            int n = geodata.length();

            for (int i = 0; i < n; ++i) {
                final JSONObject player = geodata.getJSONObject(i);
                playerObj = new Player(i+1,player.getString("firstName"),player.getString("secondName"),player.getString("webName"),player.getInt("teamCode"),
                        player.getInt("id"),player.getInt("playerPosition"),player.getDouble("cost"),player.getInt("code"));

                players.add(playerObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return players;
    }
    
    public int parseBudget(String response){
        return Integer.valueOf(response);
    }

    public boolean parseCheckInvites(String sentBy) {
        if(!sentBy.contains("You have no")){
            return true;
        }
        
        return false;

    }
    
    public ArrayList<String> parseInvites(String response){

        String[] parts = response.split("//");
        ArrayList<String> invites = new ArrayList<>();
        
        for(String s:parts){
            if(s.length() >0){
                invites.add(s);
            }
        }
        
        return invites;
    }
    
    public ArrayList<Player> parseSearchResults(String results){
        ArrayList<Player> players = new ArrayList<Player>();
        Player playerObj = null;
        JSONObject obj = null;
        boolean canAdd = true;
        try {
            obj = new JSONObject(results);
            JSONArray geodata = obj.getJSONArray("results");
            int n = geodata.length();

            for (int i = 0; i < n; ++i) {
                canAdd = true;
                final JSONObject player = geodata.getJSONObject(i);
                playerObj = new Player(i+1,player.getString("firstName"),player.getString("secondName"),player.getString("webName"),player.getInt("teamCode"),
                        player.getInt("id"),player.getInt("playerPosition"),player.getDouble("cost"),player.getInt("code"));



                for(int x=0;x < players.size();x++){
                    if(playerObj.getId() == players.get(x).getId()){
                        canAdd = false;
                        break;
                    }
                }



                if(canAdd){
                    players.add(playerObj);
                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return players;
    }
    
    public ArrayList<User> parseUserContests(String response){
        ArrayList<User> users = new ArrayList<User>();
        User userObj = null;
        JSONObject obj = null;
        boolean canAdd = true;
        try {
            obj = new JSONObject(response);
            JSONArray geodata = obj.getJSONArray("users");
            int n = geodata.length();

            for (int i = 0; i < n; ++i) {
                canAdd = true;
                final JSONObject user1 = geodata.getJSONObject(i);
                userObj = new User(user1.getString("username"),"",0,0,user1.getInt("points"));
                users.add(userObj);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return users;
    }
}
