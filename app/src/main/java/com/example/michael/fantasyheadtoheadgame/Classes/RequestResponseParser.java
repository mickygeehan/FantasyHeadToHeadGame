package com.example.michael.fantasyheadtoheadgame.Classes;

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
                        player.getInt("id"),player.getInt("playerPosition"),player.getDouble("cost"));

                players.add(playerObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return players;
    }
    
}
