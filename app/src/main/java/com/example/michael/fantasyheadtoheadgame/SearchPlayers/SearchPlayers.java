package com.example.michael.fantasyheadtoheadgame.SearchPlayers;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
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
import com.example.michael.fantasyheadtoheadgame.TestHomeScreen;

import java.util.ArrayList;

public class SearchPlayers extends Activity implements UserTeamAsyncResponse {
    
    ArrayList<Player> searchResults;
    ListView searchResultsLv;
    int sizeOfTeam = 0;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);

        
        final int numPlayers = (int) getIntent().getSerializableExtra("numberPlayers");
        final int userID = (int)getIntent().getSerializableExtra("userID");
        sizeOfTeam = numPlayers;
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
                                    int playerId = searchResults.get(itemPosition).getId();
                                    int playerPositionInTeam = sizeOfTeam +1;
                                    String url = "?player"+playerPositionInTeam+"="+playerId+"&userID="+userID;
                                    System.out.println(url);
                                    UpdateUserTeamHttpResponse getWeeklyTeam = new UpdateUserTeamHttpResponse(SearchPlayers.this,url);
                                    getWeeklyTeam.delegate = SearchPlayers.this;
                                    getWeeklyTeam.execute();
                                    sizeOfTeam = sizeOfTeam +1;
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
                searchPlayersReq = new SearchPlayerHttpRequest(this,wnStr);
            }else{
                searchPlayersReq = new SearchPlayerHttpRequest(this,wnStr,fnStr);
            }
            searchPlayersReq.delegate = this;
            searchPlayersReq.execute();
        }
    }

    private void displayToast(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void processFinish(ArrayList<Player> players){
        searchResults = players;
        ArrayList<String> playersNames = new ArrayList<>();
        //playersNames.add(0,"Cost"+"\t\t\t\t||\t\t\t\t"+"Player Name");
        
        for(Player p: players){
            playersNames.add(p.getCost()+"\t\t\t\t  \t\t\t\t"+p.getFirstName() +" "+ p.getSecondName());
        }
        
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                playersNames );
        
        searchResultsLv.setAdapter(arrayAdapter);
        
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
}
