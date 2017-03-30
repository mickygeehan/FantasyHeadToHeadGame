package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

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

import com.example.michael.fantasyheadtoheadgame.Classes.Game;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetUserMatchesHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UserContests extends AppCompatActivity implements UserTeamAsyncResponse {
    
    private ListView lv;
    private String username;
    private HashMap<String,User> matches = new HashMap<>();
    private ArrayList<Game> allGames;
    private ArrayList<String> opponents;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_matches);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        
        
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
    
    private void getOpponentsHttpCall(User user){
        GetUserMatchesHttpRequest getMatches = new GetUserMatchesHttpRequest(UserContests.this,user.getId(),user.getUsername());
        getMatches.delegate = this;
        getMatches.execute();
    }

    @Override
    public void processFinish(ArrayList<Player> players) {
        
    }

    @Override
    public void processUserUpdate(String result) {

    }

    @Override
    public void processUserMatches(ArrayList<User> users) {
        
        //Get all games used for on click
        getAllGames(users);
        
        
        //display individual contests
        ArrayList<String> opponents = getContestAgainstUsers(users);
        for(String s: opponents){

        }

        
        
        
        //display all results WORKING
//        for(int i =0;i<users.size()-1;i++){
//            matchedUsersAdap.add(users.get(i).getUsername()+" "+ users.get(i).getPoints() + " - "+ users.get(i+1).getPoints()
//                    +" "+ users.get(i+1).getUsername());
//            Game g = new Game(users.get(i),users.get(i+1));
//            allGames.add(g);
//            i++;
//            
//        }

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

    @Override
    public void processLogin(User user) {
        
    }

    @Override
    public void processInvites(String sentBy) {
        
    }

    @Override
    public void processDate(String epochDate) {
        
    }
}
