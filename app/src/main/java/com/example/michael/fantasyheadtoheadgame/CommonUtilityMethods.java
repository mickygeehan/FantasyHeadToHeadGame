package com.example.michael.fantasyheadtoheadgame;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by michaelgeehan on 30/03/2017.
 */

public class CommonUtilityMethods{

    //For displaying of notifications
    public static void displayToast(Context c, String message){
        Toast.makeText(c, message,
                Toast.LENGTH_LONG).show();
    }
    

    
}
