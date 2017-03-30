package com.example.michael.fantasyheadtoheadgame.UtilityClasses;

import android.content.Context;
import android.widget.Toast;

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

    
}
