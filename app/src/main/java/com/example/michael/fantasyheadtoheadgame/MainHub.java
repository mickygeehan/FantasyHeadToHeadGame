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
import com.example.michael.fantasyheadtoheadgame.Classes.User;

import java.util.HashMap;

public class MainHub extends AppCompatActivity {
    
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
}
