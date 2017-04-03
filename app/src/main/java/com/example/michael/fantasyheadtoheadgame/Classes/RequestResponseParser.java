package com.example.michael.fantasyheadtoheadgame.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

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
    
    
}
