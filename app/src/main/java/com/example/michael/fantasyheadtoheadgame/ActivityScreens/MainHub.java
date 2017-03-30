package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetDeadlineHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.Session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainHub extends AppCompatActivity implements UserTeamAsyncResponse {
    
    private User loggedInUser;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                finish();
            }
        });

        
        //check if user logged in
        session = new SessionManager(getApplicationContext());
        checkUserLoggedIn();
  
    }
    
    private void setUserDetails(){
        HashMap<String, String> user1Details = session.getUserDetails();
        if(user1Details.get("name") == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }else{
            loggedInUser = new User(user1Details.get("name").toString(),user1Details.get("email").toString(),Integer.valueOf(user1Details.get("ID").toString()));
            getSupportActionBar().setTitle("Welcome back "+loggedInUser.getUsername());
            //gets deadline
            getDeadline();
        }
    }
    
    private void checkUserLoggedIn(){
        //if no session it will start login or else it will set user stuff
        session.checkLogin();
        setUserDetails();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkUserLoggedIn();
    }
    
    private void getDeadline(){
        GetDeadlineHttpRequest replyH2H = new GetDeadlineHttpRequest(MainHub.this);
        replyH2H.delegate = MainHub.this;
        replyH2H.execute();
    }

    public void gotoUserTeamScreen(View view){
        Intent intent = new Intent(this, UserTeamScreen.class);
        intent.putExtra("UserClass", loggedInUser);
        startActivity(intent);
    }

    public void gotoUserMatchesScreen(View view){
        Intent intent = new Intent(this, UserContests.class);
        intent.putExtra("UserClass", loggedInUser);
        startActivity(intent);
    }

    @Override
    public void processFinish(ArrayList<Player> players) {
        
    }

    @Override
    public void processUserUpdate(String result) {

    }

    @Override
    public void processUserMatches(ArrayList<User> users) {

    }

    @Override
    public void processLogin(User user) {

    }

    @Override
    public void processInvites(String sentBy) {

    }

    @Override
    public void processDate(String epochDate) {
        
        String[] parts = epochDate.split("//");
        
        Long epochT = Long.valueOf(parts[0]);
        Date date = new Date(epochT*1000);
        
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formDate = sdf.format(date);
        
        TextView xmlDate = (TextView)findViewById(R.id.xmlDateView);
        xmlDate.setText("Deadline: \n "+formDate);
        
        
        TextView gameWeek = (TextView)findViewById(R.id.xmlGameweek);
        gameWeek.setText("Gameweek: "+parts[1]);
        

    }
}
