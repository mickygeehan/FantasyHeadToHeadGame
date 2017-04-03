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
    private ListView searchResultsLv;
    private int sizeOfTeam = 0;
    private int budget = 0;

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

        //initialise request
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
        }
        
        
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

                                    if(budget >= searchResults.get(itemPosition).getCost()){
                                        int playerId = searchResults.get(itemPosition).getId();
                                        int playerPositionInTeam = sizeOfTeam +1;
                                        String urlToAppend = "player"+playerPositionInTeam+"="+playerId+"&userID="+userID;
                                        updateUserTeamRequest(urlToAppend);
                                        sizeOfTeam = sizeOfTeam +1;
                                        budget = (int) (budget - searchResults.get(itemPosition).getCost());
                                        CommonUtilityMethods.displayToast(getApplicationContext(),"Player added to team!");
           
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
                    urlToAppend = "webName="+wnStr;
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
