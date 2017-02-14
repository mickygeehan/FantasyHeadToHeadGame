package com.example.michael.fantasyheadtoheadgame;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        context = Home.this;
        
        //get user class
        User user = (User) getIntent().getSerializableExtra("UserClass");
        
        //So in here got to see if theres a new team available
        //so check if weekID in userTeam < weeklyTeam
        //if so say new team available check your head to head games
        //if not display the weekly team in this screen and a menu button
        
        displayWeeklyTeam();
    }
    
    private void displayWeeklyTeam(){
        GetWeeklyTeamHttpRequest getWeeklyTeam = new GetWeeklyTeamHttpRequest(context);
        getWeeklyTeam.execute();
    }

    public static void onBackgroundTaskDataObtained(ArrayList<Player> players) {
        for(Player p: players){
            System.out.println(p.getFirstName());
        }
    }
    
}
