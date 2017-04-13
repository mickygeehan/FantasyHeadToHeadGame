package com.example.michael.fantasyheadtoheadgame.ActivityScreens;


import android.app.Activity;
import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.michael.fantasyheadtoheadgame.Classes.MySingleton;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.UpdateUserTeamHttpResponse;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.SearchPlayerHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchPlayers extends Activity{
    
    //Global Variables
    private ArrayList<Player> searchResults;
    private ArrayList<Player> usersPlayers;
    private ListView searchResultsLv;
    private int sizeOfTeam = 0;
    private int budget = 0;
    private boolean playerBought = false;
    private Player playerIn = null;

    //Volley variables
    private RequestQueue queue;
    private RequestResponseParser responseParser;
    private ProgressDialog progressD;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_players);
        
        //initialise passed over variables
        sizeOfTeam = (int) getIntent().getSerializableExtra("numberPlayers");
        final int userID = (int)getIntent().getSerializableExtra("userID");
        budget =  (int)getIntent().getSerializableExtra("budget");
        usersPlayers = (ArrayList<Player>)getIntent().getSerializableExtra("usersTeam");
        


        //initialise request
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
        }

        getActionBar().setTitle("Budget: "+budget);
        
        
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
                                    //Check have enough money
                                    if(budget >= searchResults.get(itemPosition).getCost()){
                                        //check player not in team
                                        if(!notInTeam(searchResults.get(itemPosition).getWebName())){
                                            int playerId = searchResults.get(itemPosition).getId();
                                            int playerPos = searchResults.get(itemPosition).getPlayerPosition();
                                            playerIn = searchResults.get(itemPosition);
                                            budget = (int) (budget - searchResults.get(itemPosition).getCost());

                                            //Get sub options in current team
                                            ArrayList<String> replaceOptions = getSubOptions(playerPos);

                                            //Show the replace dialog
                                            showReplaceDialog(ct,replaceOptions);
                                        }else{
                                            CommonUtilityMethods.displayToast(getApplicationContext(),"Player already in team!");
                                        }
                                        
                                        
           
                                    }else{
                                        CommonUtilityMethods.displayToast(getApplicationContext(),"Not enough money!");
                                    }
                                   
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_input_get)
                            .setCancelable(false)
                            .show();
                }
                
            }
        });
        
    }
    
    private boolean notInTeam(String toCheck){
        for(Player p : usersPlayers){
            if(p.getWebName().equals(toCheck)){
                return true;
            }
        }
        return false;
    }
    
    private void showReplaceDialog(ContextThemeWrapper ct,ArrayList<String> playerOptions){
        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(SearchPlayers.this, android.R.layout.select_dialog_singlechoice);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.mylist,
                playerOptions);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        builder.setTitle("Select player to replace!");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                String playerToReplace = arrayAdapter.getItem(item);
                
                int i = 0;
                int toRemove = 0;
                int posInTeam = 0;
                for(Player p: usersPlayers){
                    if(p.getWebName().equals(playerToReplace)){
                        posInTeam = p.getPosInTeam();
                        p.setPosInTeam(posInTeam);
                        toRemove = i;
                    }
                    i++;
                }
                //remove replace plaer and set position to new player
                usersPlayers.remove(toRemove);
                usersPlayers.get(usersPlayers.size()-1).setPosInTeam(posInTeam);

                CommonUtilityMethods.displayToast(getApplicationContext(),"You have subbed in this player!");
                
                
            }
        });
        AlertDialog alert = builder.create(); 
        alert.setCancelable(false);
        alert.show();

        usersPlayers.add(playerIn);
  
    }
    
    private ArrayList<String> getSubOptions(int playerPos){
        ArrayList<String> subOptions = new ArrayList<>();
        
        for(Player p: usersPlayers){
            if(p.getPlayerPosition() == playerPos){
                subOptions.add(p.getWebName());
            }
        }
        
        return subOptions;
    }

    private boolean updateUserTeamRequest(String addedUrl){
        String url =Constants.UPDATE_USERTEAM_ADDRESS+addedUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        // CommonUtilityMethods.displayToast(getApplicationContext(),response);
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

    private boolean searchPlayerRequest(String urlToAppend){
        String url = Constants.SEARCH_ADDRESS+urlToAppend;
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        ArrayList<Player> searchResults = responseParser.parseSearchResults(response);
                        processFinish(searchResults);
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
    
    //prevents sql injection
    public boolean isCleanInput(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }

    private boolean initialiseParser(){
        responseParser = (RequestResponseParser) getIntent().getSerializableExtra("parser");
        if(responseParser!=null ){
            return true;
        }else{
            return false;

        }
    }
    
    public void searchForPlayer(View view){
        String urlToAppend ="";
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
                    urlToAppend = "webName="+wnStr+"&firstName= ";
                }
            }else{
                if(isCleanInput(wnStr)){
                    if(isCleanInput(fnStr)){
                        urlToAppend = "webName="+wnStr+"&firstName="+fnStr;
                    }
                }
            }
            searchPlayerRequest(urlToAppend);
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
        i.putExtra("usersTeam",usersPlayers);
        setResult(RESULT_OK, i);
        finish();
    }
    

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

   
}
