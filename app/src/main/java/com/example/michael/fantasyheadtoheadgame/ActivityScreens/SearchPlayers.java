package com.example.michael.fantasyheadtoheadgame.ActivityScreens;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.UpdateUserTeamHttpResponse;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.SearchPlayerHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import java.util.ArrayList;

public class SearchPlayers extends Activity implements UserTeamAsyncResponse {
    
    //Global Variables
    private ArrayList<Player> searchResults;
    private ListView searchResultsLv;
    private int sizeOfTeam = 0;
    private int budget = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);
        
        //initialise passed over variables
        sizeOfTeam = (int) getIntent().getSerializableExtra("numberPlayers");
        final int userID = (int)getIntent().getSerializableExtra("userID");
        budget =  (int)getIntent().getSerializableExtra("budget");
        
        
        //initialising the listview and code for on click of listview
        final ContextThemeWrapper ct = new ContextThemeWrapper(this, R.style.myDialog);
        //item click
        searchResultsLv = (ListView) findViewById(R.id.xmlListView);
        searchResultsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listView, View itemView, final int itemPosition, long itemId)
            {
                if(sizeOfTeam >=18){
                    Toast.makeText(getApplicationContext(),"You have maxed out your team",
                            Toast.LENGTH_LONG).show();
                }else{
                    new AlertDialog.Builder(ct)
                            .setTitle("Buy Player")
                            .setMessage("Are you sure you want to buy "+ searchResults.get(itemPosition).getWebName())
                            .setPositiveButton("Buy", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with buy
                                    System.out.println(budget);
                                    System.out.println(searchResults.get(itemPosition).getCost());
                                    if(budget >= searchResults.get(itemPosition).getCost()){
                                        int playerId = searchResults.get(itemPosition).getId();
                                        int playerPositionInTeam = sizeOfTeam +1;
                                        String url = "?player"+playerPositionInTeam+"="+playerId+"&userID="+userID;
                                        System.out.println(url);
                                        UpdateUserTeamHttpResponse getWeeklyTeam = new UpdateUserTeamHttpResponse(SearchPlayers.this,url);
                                        getWeeklyTeam.delegate = SearchPlayers.this;
                                        getWeeklyTeam.execute();
                                        sizeOfTeam = sizeOfTeam +1;
                                        budget = (int) (budget - searchResults.get(itemPosition).getCost());
           
                                    }else{
                                        Toast.makeText(getApplicationContext(),"Not enough money",Toast.LENGTH_LONG).show();
                                    }
                                   
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_input_get)
                            .show();
                }
                
            }
        });
    }

    //prevents sql injection
    public boolean isCleanInput(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }


    public void searchForPlayer(View view){
        EditText firstName = (EditText)findViewById(R.id.xmlFirstNameQuery);
        EditText webName = (EditText)findViewById(R.id.xmlSecondNameQuery);
        String fnStr = firstName.getText().toString();
        String wnStr = webName.getText().toString();
        SearchPlayerHttpRequest searchPlayersReq;
        
        //Checking serach queries arent empty
        if(fnStr.isEmpty() && wnStr.isEmpty()){
            displayToast("Please enter a search query");
        }else{
            
            if(fnStr.isEmpty()){
                if(isCleanInput(wnStr)){
                    searchPlayersReq = new SearchPlayerHttpRequest(this,wnStr);
                    searchPlayersReq.delegate = this;
                    searchPlayersReq.execute();
                }
                
            }else{
                if(isCleanInput(wnStr)){
                    if(isCleanInput(fnStr)){
                        searchPlayersReq = new SearchPlayerHttpRequest(this,wnStr,fnStr);
                        searchPlayersReq.delegate = this;
                        searchPlayersReq.execute();
                    }
                }
                
            }
            
        }
    }

    private void displayToast(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        i.putExtra("budget", budget);
        setResult(RESULT_OK, i);
        finish();
    }
    
    @Override
    public void processFinish(ArrayList<Player> players){
        searchResults = players;
        ArrayList<String> playersNames = new ArrayList<>();
        
        if(players != null){
            for(Player p: players){
                playersNames.add("€"+p.getCost()+"\t\t\t\t  \t\t\t\t"+p.getFirstName() +" "+ p.getSecondName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    playersNames );

            searchResultsLv.setAdapter(arrayAdapter);
        }
       
        
    }

    @Override
    public void processUserUpdate(String result) {
        Toast.makeText(getApplicationContext(),"Player Added to team",
                Toast.LENGTH_LONG).show();
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
        
    }
}
