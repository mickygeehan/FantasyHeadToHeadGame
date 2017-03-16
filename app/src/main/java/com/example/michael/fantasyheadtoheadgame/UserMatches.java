package com.example.michael.fantasyheadtoheadgame;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.FindHeadToHeadMatchHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetUserMatchesHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class UserMatches extends AppCompatActivity implements UserTeamAsyncResponse {
    
    private ListView lv;
    private ArrayList<User> matchedUsers;
    private ArrayList<String> matchedUsersAdap = new ArrayList<>();
    private String username;
    private HashMap<String,User> matches = new HashMap<>();
    private User currentUser;

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
        
        lv = (ListView)findViewById(R.id.xmlUMlistview);
        User user = (User) getIntent().getSerializableExtra("UserClass");
        username = user.getUsername();
        GetUserMatchesHttpRequest getMatches = new GetUserMatchesHttpRequest(UserMatches.this,user.getId(),user.getUsername());
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
//        currentUser = new User(username,"",0,0,0);
//
//        for(User u: users){
//            System.out.println(u.getUsername()+u.getPoints());
//            matchedUsersAdap.add(currentUser.getPoints()+" "+currentUser.getUsername()+" VS "+u.getUsername()+" "+u.getPoints());
//        }
        
        
        //display all results
        for(int i =0;i<users.size()-1;i++){
            matchedUsersAdap.add(users.get(i).getUsername()+" "+ users.get(i).getPoints() + " - "+ users.get(i+1).getPoints()
                    +" "+ users.get(i+1).getUsername());
            i++;
        }
//        matchedUsers = users;
//        for(User u: users){
//            if(u != null){
//                matches.put(u.getUsername(),u);
//                matchedUsersAdap.add(u.getUsername() +" "+u.getPoints()+" VS "+matches.get(username).getPoints()+" "+ username);
//            }
//            
//        }

        if(matchedUsersAdap.size() > 0) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    R.layout.mylist,
                    matchedUsersAdap);

            lv.setAdapter(arrayAdapter);
        }
        
    }

    @Override
    public void processLogin(User user) {
        
    }
}
