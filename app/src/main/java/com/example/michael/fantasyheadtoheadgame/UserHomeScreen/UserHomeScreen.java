package com.example.michael.fantasyheadtoheadgame.UserHomeScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetUserTeamHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.ActivityScreens.SearchPlayers;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.UpdateUserTeamHttpResponse;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UserHomeScreen extends AppCompatActivity implements UserTeamAsyncResponse {

    private Context context;
    private Spinner weeklyTeamSpinner;
    private Spinner playerSubOptions;
    private int userID = -1;
    // private static ArrayList<Player> players;
    private int numGk = 2;
    private int numDef,numMid,numAtt = 0;

    ArrayList<Player> playersInTeam;
    HashMap<String, Player> playerMap;
    ArrayList<String> nameSpinner;
    Player p;
    ArrayList<String> playerSubOptionsAdap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_screen);
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
        
        

        //initialise variables
        context = UserHomeScreen.this;
        weeklyTeamSpinner = (Spinner)findViewById(R.id.xmlWeeklyTSpinner);

        //get user class
        User user = (User) getIntent().getSerializableExtra("UserClass");
        //getActionBar().setTitle(user.getUsername()+"'s team");
        getSupportActionBar().setTitle(user.getUsername()+"'s team");
        userID = user.getId();
        TextView userName = (TextView)findViewById(R.id.xmlDisplayUserName);
        userName.setText("Welcome back "+user.getUsername());

        //So in here got to see if theres a new team available
        //so check if weekID in userTeam < weeklyTeam
        //if so say new team available check your head to head games
        //if not display the weekly team in this screen and a menu button

        displayWeeklyTeam(userID);

        weeklyTeamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String playerName = nameSpinner.get(position);
                p = playerMap.get(playerName);
                updatePlayerStats(p);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }


    private void updatePlayerStats(Player p){
        playerSubOptionsAdap = new ArrayList<String>();
        TextView playerName = (TextView)findViewById(R.id.xmlFullName);
        TextView playerCost = (TextView)findViewById(R.id.xmlCost);
        TextView playerTeam = (TextView)findViewById(R.id.xmlTeam);
        TextView playerPosInTeam = (TextView)findViewById(R.id.xmlPositionInTeam);
        playerSubOptions = (Spinner)findViewById(R.id.xmlSpinnerChangePlayer);


        playerName.setText(p.getFirstName()+" "+p.getSecondName());
        playerCost.setText(Double.toString(p.getCost()));
        playerTeam.setText(String.valueOf(p.getTeamCode()));
        playerPosInTeam.setText(String.valueOf(p.getPosInTeam()));

        for(Player pl: playersInTeam){
            if(pl.getPlayerPosition() == p.getPlayerPosition()){
                if(pl.getPosInTeam() > 11){
                    playerSubOptionsAdap.add(pl.getWebName());
                }
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,playerSubOptionsAdap);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        playerSubOptions.setAdapter(adapter);



    }

    public void changePlayer(View view){
        //Get currennt player in team
        String playerName = weeklyTeamSpinner.getSelectedItem().toString();
        Player currentPlayer = playerMap.get(playerName);
        int currentPosInTeam = currentPlayer.getPosInTeam();


        //get player to sub in
        playerName = playerSubOptions.getSelectedItem().toString();
        Player playerToSwap = playerMap.get(playerName);
        int playerToSwapPosInTeam = playerToSwap.getPosInTeam();


        //setIDs
        currentPlayer.setPosInTeam(playerToSwapPosInTeam);
        playerToSwap.setPosInTeam(currentPosInTeam);

        //remove old players them back to arraylist of players
//        int i = 0;
//        for(Player p: playersInTeam){
//            if(p.getPosInTeam() == playerToSwapPosInTeam || p.getPosInTeam() == currentPosInTeam){
//                playersInTeam.remove(i);
//                i++;
//            }
//        }

        //re add them
//        playersInTeam.add(currentPlayer);
//        playersInTeam.add(playerToSwap);


        Toast.makeText(getApplicationContext(),"You have now subbed in this player!",
                Toast.LENGTH_LONG).show();

        updateUserTeamSpinner();



    }

    private void updateUserTeamSpinner(){

        //sort them

        boolean playerAdded = false;
        int x=1;
        int temp = 0;

        nameSpinner.clear();

        //sort function
        for(int i=0;i < playersInTeam.size();i++){

            while(!playerAdded){

                if(playersInTeam.get(temp).getPosInTeam() == x){
                    nameSpinner.add(playersInTeam.get(temp).getWebName());
                    playerAdded =true;
                    temp = 0;
                    x = x + 1;
                }else{
                    temp = temp + 1;
                }

            }
            playerAdded = false;

        }



        //display
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,nameSpinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        weeklyTeamSpinner.setAdapter(adapter);

    }


    private void displayWeeklyTeam(int id){
        GetUserTeamHttpRequest getWeeklyTeam = new GetUserTeamHttpRequest(context,id);
        getWeeklyTeam.delegate = this;
        getWeeklyTeam.execute();
    }

    public void searchScreen(View view){
        Intent intent = new Intent(UserHomeScreen.this, SearchPlayers.class);
        startActivity(intent);
    }
    

    public void updateUserTeamInDB(View view){
        String url = "?";
        int i = 1;
//        for(Player p : playersInTeam){
//            url = url + "player"+i+"ID="+p.getId()+"&";
//        }
        for(String name : nameSpinner){
            Player pl = playerMap.get(name);
            url = url + "player"+i+"="+pl.getId()+"&";
            i++;
        }
        url = url+"userID="+userID;

        UpdateUserTeamHttpResponse getWeeklyTeam = new UpdateUserTeamHttpResponse(context,url);
        getWeeklyTeam.delegate = this;
        getWeeklyTeam.execute();
    }

    @Override
    public void processFinish(ArrayList<Player> players) {

        nameSpinner = new ArrayList<String>();
        playerMap = new HashMap<String,Player>();

        playersInTeam = players;

        for(Player p: players){
            nameSpinner.add(p.getWebName());
            playerMap.put(p.getWebName(),p);
            if(p.getPlayerPosition() == 2){
                numDef = numDef+1;
            }else if(p.getPlayerPosition() == 3){
                numMid = numMid+1;
            }else if(p.getPlayerPosition() == 4){
                numAtt = numAtt+1;
            }
        }

        System.out.println(String.valueOf(numDef)+" "+String.valueOf(numMid)+" "+String.valueOf(numAtt));

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item,nameSpinner);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        weeklyTeamSpinner.setAdapter(adapter);
    }

    @Override
    public void processUserUpdate(String result) {
        Toast.makeText(getApplicationContext(),result,
                Toast.LENGTH_LONG).show();
    }
}



