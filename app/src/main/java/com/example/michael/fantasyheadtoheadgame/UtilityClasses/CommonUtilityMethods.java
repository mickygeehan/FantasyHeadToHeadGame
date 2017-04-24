package com.example.michael.fantasyheadtoheadgame.UtilityClasses;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by michaelgeehan on 30/03/2017.
 */

public class CommonUtilityMethods{

    //For displaying of notifications
    public static void displayToast(Context c, String message){
        Toast.makeText(c, message,
                Toast.LENGTH_LONG).show();
    }

    //checking IP address valid
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    //Request method and return result - New updated way using volley
    public static String sendURLRequest(RequestQueue queue,String url, final Context c){
        final String[] responseToreturn = {""};
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        responseToreturn[0] = response;
                        CommonUtilityMethods.displayToast(c,response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseToreturn[0] = "";
            }
        });
        queue.add(stringRequest);
        return responseToreturn[0];
    }
    
}
