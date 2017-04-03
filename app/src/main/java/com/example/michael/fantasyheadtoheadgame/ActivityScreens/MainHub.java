package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.michael.fantasyheadtoheadgame.Classes.MySingleton;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetDeadlineHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.Session.SessionManager;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainHub extends AppCompatActivity{
    
    private User loggedInUser;
    private SessionManager session;
    private RequestQueue queue;
    private RequestResponseParser responseParser;
    private ProgressDialog progressD;

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

        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();


        //queue = Volley.newRequestQueue(this);
        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
        }
        
        //check if user logged in
        session = new SessionManager(getApplicationContext());
        checkUserLoggedIn();
  
    }
    
    private boolean initialiseParser(){
        responseParser = (RequestResponseParser) getIntent().getSerializableExtra("parser");
        if(responseParser!=null ){
            return true;
        }else{
            return false;
            
        }
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
        //start loading bar
        startProgressBar();
        
        //start url request
        String url ="http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/GetDeadline.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        progressD.cancel();
                        processDate(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressD.cancel();
                CommonUtilityMethods.displayToast(getApplicationContext(),"There seems to be a server issue");
            }
        });
        queue.add(stringRequest);
    }
    
    private boolean startProgressBar(){
        progressD = new ProgressDialog(this);
        if(progressD != null){
            progressD.setMessage("Loading...");
            progressD.show();
            return true;
        }
        
        return false;
        
    }
    
    public void gotoUserTeamScreen(View view){
        Intent intent = new Intent(this, UserTeamScreen.class);
        intent.putExtra("UserClass", loggedInUser);
        intent.putExtra("parser",responseParser);
        startActivity(intent);
    }

    public void gotoUserMatchesScreen(View view){
        Intent intent = new Intent(this, UserContests.class);
        intent.putExtra("UserClass", loggedInUser);
        startActivity(intent);
    }
    
    public boolean processDate(String epochDate){
        boolean toReturn = false;
        
        if(epochDate.contains("//")){
            String[] parts = epochDate.split("//");

            Long epochT = Long.valueOf(parts[0]);
            Date date = new Date(epochT*1000);


            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String formDate = sdf.format(date);

            TextView xmlDate = (TextView)findViewById(R.id.xmlDateView);
            xmlDate.setText("Deadline: \n "+formDate);


            TextView gameWeek = (TextView)findViewById(R.id.xmlGameweek);
            gameWeek.setText("Gameweek: "+parts[1]);
            toReturn = true;
        }else{
            toReturn = false;
        }
        
        return toReturn;
    }
}
