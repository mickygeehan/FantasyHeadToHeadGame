package com.example.michael.fantasyheadtoheadgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.michael.fantasyheadtoheadgame.Activities.Login;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetDeadlineHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.ReplyToInviteHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainHub extends AppCompatActivity implements UserTeamAsyncResponse {
    
    private User user;
    SharedPreferences mPrefs;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());
        
        

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                finish();
            }
        });

        
        session.checkLogin();
        
        HashMap<String, String> user1Details = session.getUserDetails();

        if(user1Details.get("name") == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }else{
            //user = (User) getIntent().getSerializableExtra("UserClass");
            user = new User(user1Details.get("name").toString(),user1Details.get("email").toString(),Integer.valueOf(user1Details.get("ID").toString()));

//            TextView userView = (TextView)findViewById(R.id.xmlMHDisplayUsername);
//            userView.setText("Welcome "+user.getUsername());
            getSupportActionBar().setTitle("Welcome back "+user.getUsername());
            //gets deadline
            getDeadline();
        }
        
       
        
        
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        session.checkLogin();
        

        HashMap<String, String> user1Details = session.getUserDetails();

        if(user1Details.get("name") == null){
            Intent i = new Intent(this, Login.class);
            startActivity(i);
            finish();
        }else{
            //user = (User) getIntent().getSerializableExtra("UserClass");
            user = new User(user1Details.get("name").toString(),user1Details.get("email").toString(),Integer.valueOf(user1Details.get("ID").toString()));

//            TextView userView = (TextView)findViewById(R.id.xmlMHDisplayUsername);
//            userView.setText("Welcome "+user.getUsername());
            getSupportActionBar().setTitle("Welcome back "+user.getUsername());
        }

        //getDeadline();
    }
    
    private void getDeadline(){
        GetDeadlineHttpRequest replyH2H = new GetDeadlineHttpRequest(MainHub.this);
        replyH2H.delegate = MainHub.this;
        replyH2H.execute();
    }

    public void gotoUserTeamScreen(View view){
        Intent intent = new Intent(this, TestHomeScreen.class);
        intent.putExtra("UserClass", user);
        startActivity(intent);
    }

    public void gotoUserMatchesScreen(View view){
        Intent intent = new Intent(this, UserMatches.class);
        intent.putExtra("UserClass", user);
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
        System.out.println(epochDate);
        
        Long epochT = Long.valueOf(epochDate);
        Date date = new Date(epochT*1000);
        
        System.out.println(date.getTime());


        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formDate = sdf.format(date);
        
        TextView xmlDate = (TextView)findViewById(R.id.xmlDateView);
        xmlDate.setText("Deadline: \n "+formDate);

    }
}
