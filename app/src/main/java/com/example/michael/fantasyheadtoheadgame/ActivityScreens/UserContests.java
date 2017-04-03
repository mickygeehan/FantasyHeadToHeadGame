package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.michael.fantasyheadtoheadgame.Classes.Game;
import com.example.michael.fantasyheadtoheadgame.Classes.MySingleton;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetUserMatchesHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class UserContests extends AppCompatActivity{
    
    private ListView lv;
    private String username;
    private HashMap<String,User> matches = new HashMap<>();
    private ArrayList<Game> allGames;
    private ArrayList<String> opponents;

    //Volley variables
    private RequestQueue queue;
    private RequestResponseParser responseParser;
    private ProgressDialog progressD;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_matches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialise request
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
        }

        
        //finds the listview to display opponents
        lv = (ListView)findViewById(R.id.xmlUMlistview);
        
        //Gets current users info from last activity & sets variable to be used in class
        User user = (User) getIntent().getSerializableExtra("UserClass");
        username = user.getUsername();
        
        //method to get all opponents
        getOpponentsHttpCall(user);
        
        
        
        //ListView on click an opponent start a new screen for list of games
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, final int itemPosition, long itemId) {
                String opponentSelected = opponents.get(itemPosition);
                ArrayList<Game> toSend = new ArrayList<>();
                for(Game g: allGames){
                    if(g.getUser1().getUsername().equals(opponentSelected)||g.getUser2().getUsername().equals(opponentSelected)){
                        toSend.add(g);
                    }
                }
                
                //Send to individual screen to display all matches
                Intent i = new Intent(getApplicationContext(), SpecifiedUserContest.class);
                i.putExtra("listGames", toSend);
                i.putExtra("opponent",opponentSelected);
                i.putExtra("currentUser",username);
                startActivity(i);
                
                
            }
        });
        
        
        
        
        
        
    }

    private boolean initialiseParser(){
        responseParser = (RequestResponseParser) getIntent().getSerializableExtra("parser");
        if(responseParser!=null ){
            return true;
        }else{
            return false;

        }
    }
    
    private boolean getOpponentsHttpCall(User user){

        String url = Constants.GET_USER_CONTESTS+"userID="+user.getId()+"&username="+user.getUsername();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        ArrayList<User> users = responseParser.parseUserContests(response);
                        processUserMatches(users);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CommonUtilityMethods.displayToast(getApplicationContext(),"There seems to be a server issue");
            }
        });
        queue.add(stringRequest);
        return true;
    }

    public void processUserMatches(ArrayList<User> users) {
        
        //Get all games used for on click
        getAllGames(users);
        
        
        //display individual contests
        ArrayList<String> opponents = getContestAgainstUsers(users);
        for(String s: opponents){

        }
        
        //sets the listview to display opponents
        if(opponents.size() > 0) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.mylist,
                    opponents);

            lv.setAdapter(arrayAdapter);
        }
        
    }

    private void getAllGames(ArrayList<User> users){
        allGames = new ArrayList<>();
        for(int i =0;i<users.size()-1;i++){
            Game g = new Game(users.get(i),users.get(i+1));
            allGames.add(g);
            i++;
        }
    }

    private ArrayList<String> getContestAgainstUsers(ArrayList<User> users){
        
        opponents = new ArrayList<>();
        
        for(User u : users){
            //check not the current user
            if(!u.getUsername().equals(username)){
                if(!opponents.contains(u.getUsername())){
                    opponents.add(u.getUsername());
                }
            }
        }
        
        
        return opponents;
    }

}
