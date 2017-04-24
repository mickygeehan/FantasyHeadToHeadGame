package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.michael.fantasyheadtoheadgame.Classes.MySingleton;
import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class UserTeamScreen extends AppCompatActivity  {
    
    //User class
    private User globalUser;
    
    //user players
    private ArrayList<Player> playersInTeam;
    private HashMap<String, Player> playerMap;
    private ArrayList<String> playersNames;
    private ArrayList<String> subOptions;
    
    //num of players in pos
    private int numDef,numMid,numAtt = 0;
    private double budget = 0;

    //For volley requests
    private RequestQueue queue;
    private RequestResponseParser responseParser;
    private ProgressDialog progressD;
    

    //Override methods from Android Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saved your team", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                generateUpdateTeamURL(globalUser.getBudget());
            }
        });

        getSupportActionBar().setTitle("Your Team");
        
        //initialise request
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();
        
        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
        }
        
        //get user
        User user = (User) getIntent().getSerializableExtra("UserClass");
        globalUser = user;

        startProgressBar();
       // getHeadToHeadInvitesRequest();
        getBudgetRequest();
        callGetUserTeamRequest(globalUser.getId());
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        
        //callGetUserTeamRequest(globalUser.getId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                double updateBudget=data.getDoubleExtra("budget",0);
                
                //initialise all new fields with new player
                playersInTeam = (ArrayList<Player>)data.getSerializableExtra("usersTeam");
                initialiseTeamVariables(playersInTeam);
                sortPlayerNames();
                initialiseFields();
                globalUser.setBudget(updateBudget);

            }
        }
    }
    
    
    //Data requests using Volley
    private boolean getBudgetRequest(){
        String url =Constants.BUDGET_ADDRESS+"userID="+ globalUser.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        //processUserUpdate(response);
                        if(response != ""){
                            budget = responseParser.parseBudget(response);
                            globalUser.setBudget(budget);
                            //getSupportActionBar().setTitle("Your Team \t\t\t\t\t\t\tBudget: "+budget);
                        }
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

    private boolean getHeadToHeadInvitesRequest(){
        String url =Constants.CHECKINVITES_ADDRESS+"username="+ globalUser.getUsername()+"&userID="+ globalUser.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        
                        if(responseParser.parseCheckInvites(response)){
                            showInvitePopup(response);
                        }
                        
                        
                        
                        //processInvites(response);
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

    private boolean callGetUserTeamRequest(int userID){
        String url =Constants.USERTEAM_ADDRESS+"userID="+ globalUser.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        ArrayList<Player> usersPlayers = responseParser.parseUserTeamResponse(response);
                        //once got players then initialise fields
                        initialiseTeamVariables(usersPlayers);
                        initialiseFields();
                        progressD.cancel();
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

    private boolean replyToInviteRequest(String sentBy){
        String url =Constants.REPLY_TO_INVITE_ADDRESS+"acceptedUserID="+ globalUser.getId()+"&fromUsername="+sentBy+"&toUsername="+ globalUser.getUsername();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        CommonUtilityMethods.displayToast(getApplicationContext(),"Invite accepted!");
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

    private boolean findRandomContestRequest(){
        String url =Constants.FINDCONTEST_ADDRESS+"username="+ globalUser.getUsername()+"&userID="+ globalUser.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        CommonUtilityMethods.displayToast(getApplicationContext(),response);
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

    private boolean sendInviteToUserRequest(String opponent){
        String url =Constants.SENDINVITE_ADDRESS+"fromUsername="+ globalUser.getUsername()+"&userID="+ globalUser.getId()+"&toUsername="+opponent;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        CommonUtilityMethods.displayToast(getApplicationContext(),response);
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
    
    
    //Button click methods
    public void openSearchScreen(View view){
        Intent intent = new Intent(UserTeamScreen.this, SearchPlayers.class);
        intent.putExtra("numberPlayers", playersInTeam.size());
        intent.putExtra("userID", globalUser.getId());
        intent.putExtra("budget", globalUser.getBudget());
        intent.putExtra("parser",responseParser);
        intent.putExtra("usersTeam",playersInTeam);
        startActivityForResult(intent,1);
    }
    
    public void findHeadToHead(View view){
        final EditText input = new EditText(this);
        input.setHint("Friends Username");
        AlertDialog.Builder builder = new AlertDialog.Builder(UserTeamScreen.this);
        builder.setView(input);
        builder.setTitle("Find H2H Contest")
                .setNeutralButton("Send Invite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendInviteToUserRequest(input.getText().toString());
                    }

                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                })
                .setNegativeButton("Random", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        findRandomContestRequest();
                    }

                });
        builder.show();

    }

    public void generateUpdateTeamURL(double budget){

        sortPlayerNames();
        String url = "";
        int i = 1;

        for(String name : playersNames){
            Player pl = playerMap.get(name);
            url = url + "player"+i+"="+pl.getId()+"&";
            i++;
        }
        url = url+"userID="+ globalUser.getId()+"&budget="+budget;

        updateUserTeamRequest(url);

    }

    public void changePlayer(View view){

        //get name
        TextView playerLabel = null;
        Player selectedPlayer = null;


        switch(view.getId())
        {
            case R.id.xmlchangePlayer1Bt:
                // Code for button 1 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer1Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;

            case R.id.imageButton4:
                // Code for button 2 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer2Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;

            case R.id.imageButton5:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer3Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton6:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer4Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton7:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer5Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton8:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer6Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton9:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer7Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton10:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer8Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton11:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer9Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton12:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer10Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());
                break;
            case R.id.imageButton13:
                // Code for button 3 click
                playerLabel = (TextView)findViewById(R.id.xmlPlayer11Name);
                selectedPlayer = playerMap.get(playerLabel.getText().toString());

//                
                break;
        }

        //clear old sub options and get new ones for new player
        subOptions.clear();
        getSubOptions(selectedPlayer);

        //displaypopup
        showStats(selectedPlayer,playerLabel);

    }
    
    
    //initialise methods
    public void initialiseTeamVariables(ArrayList<Player> players) {
        //initialise variables
        playersInTeam = new ArrayList<>();
        playersNames = new ArrayList<>();
        playerMap = new HashMap<>();
        subOptions = new ArrayList<>();

        //make sure players doesn't go over 11 to remove sub feature
        //to be full removed upon release
        int max = 10;
        int i =0;
        for(Player p: players){
            if(i <= 10){
                playersInTeam.add(p);
                playersNames.add(p.getWebName());
                playerMap.put(p.getWebName(),p);
                if(p.getPlayerPosition() == 2){
                    numDef = numDef+1;
                }else if(p.getPlayerPosition() == 3){
                    numMid = numMid+1;
                }else if(p.getPlayerPosition() == 4){
                    numAtt = numAtt+1;
                }
            }
            i++;
            
        }
    }
    
    private boolean initialiseParser(){
        responseParser = (RequestResponseParser) getIntent().getSerializableExtra("parser");
        if(responseParser!=null ){
            return true;
        }else{
            return false;

        }
    }

    private void initialiseFields(){
        TextView player1Label = (TextView)findViewById(R.id.xmlPlayer1Name);
        TextView player2Label = (TextView)findViewById(R.id.xmlPlayer2Name);
        TextView player3Label = (TextView)findViewById(R.id.xmlPlayer3Name);
        TextView player4Label = (TextView)findViewById(R.id.xmlPlayer4Name);
        TextView player5Label = (TextView)findViewById(R.id.xmlPlayer5Name);
        TextView player6Label = (TextView)findViewById(R.id.xmlPlayer6Name);
        TextView player7Label = (TextView)findViewById(R.id.xmlPlayer7Name);
        TextView player8Label = (TextView)findViewById(R.id.xmlPlayer8Name);
        TextView player9Label = (TextView)findViewById(R.id.xmlPlayer9Name);
        TextView player10Label = (TextView)findViewById(R.id.xmlPlayer10Name);
        TextView player11Label = (TextView)findViewById(R.id.xmlPlayer11Name);

        player1Label.setText(playersNames.get(0));
        player2Label.setText(playersNames.get(1));
        player3Label.setText(playersNames.get(2));
        player4Label.setText(playersNames.get(3));
        player5Label.setText(playersNames.get(4));
        player6Label.setText(playersNames.get(5));
        player7Label.setText(playersNames.get(6));
        player8Label.setText(playersNames.get(7));
        player9Label.setText(playersNames.get(8));
        player10Label.setText(playersNames.get(9));
        player11Label.setText(playersNames.get(10));

        initialiseImages();

    }

    private void initialiseImages(){

        for(int i=1; i <=10;i++){

            Player p = playerMap.get(playersNames.get(i));
            int playerTeam = p.getTeamCode();
            setImage(playerTeam,i);


        }


    }
    
    
    //methods set the images based on team id and position in team
    //for eg. if player pos = 2 then gets imagebutton 4
    private ImageButton getImageButton(int posInTeam){
        ImageButton im = null;

        switch(posInTeam){
            case 1:
                im = (ImageButton)findViewById(R.id.imageButton4);
                break;
            case 2:
                im = (ImageButton)findViewById(R.id.imageButton5);
                break;
            case 3:
                im = (ImageButton)findViewById(R.id.imageButton6);
                break;
            case 4:
                im = (ImageButton)findViewById(R.id.imageButton7);
                break;
            case 5:
                im = (ImageButton)findViewById(R.id.imageButton8);
                break;
            case 6:
                im = (ImageButton)findViewById(R.id.imageButton9);
                break;
            case 7:
                im = (ImageButton)findViewById(R.id.imageButton10);
                break;
            case 8:
                im = (ImageButton)findViewById(R.id.imageButton11);
                break;
            case 9:
                im = (ImageButton)findViewById(R.id.imageButton12);
                break;
            case 10:
                im = (ImageButton)findViewById(R.id.imageButton13);
                break;
            default:
                break;

        }

        return im;
    }
    
    private void setImage(int playerTeam,int posInTeam){
        ImageButton im = null;
        switch(playerTeam){
            case 8:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.chelsea);
                break;
            case 91:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.bournmeth);
                break;
            case 3:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.arsenal);
                break;
            case 31:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.crystal_palace);
                break;
            case 11:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.everton);
                break;
            case 88:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.hull);
                break;
            case 13:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.leicester);
                break;
            case 14:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.liverpool);
                break;
            case 43:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.man_city);
                break;
            case 1:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.man_united);
                break;
            case 25:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.middlesborough);
                break;
            case 20:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.southampton);
                break;
            case 110:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.stoke);
                break;
            case 56:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.sunderland);
                break;
            case 80:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.swansea);
                break;
            case 6:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.tottenham);
                break;
            case 57:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.watford);
                break;
            case 35:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.west_brom);
                break;
            case 21:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.west_ham);
                break;
            case 90:
                im = getImageButton(posInTeam);
                im.setImageResource(R.drawable.burnley);
                break;
            default:
                break;
        }
    }


    //general methods & popups
    private void sortPlayerNames(){
        //this function sorts the players in users team by position in team
        //This is to ensure that the players are put into the db in the right position

        boolean playerAdded = false;
        int x=1;
        int temp = 0;
        playersNames.clear();

        //sort function
        for(int i=0;i < playersInTeam.size();i++){
            while(!playerAdded){
                if(playersInTeam.get(temp).getPosInTeam() == x){
                    playersNames.add(playersInTeam.get(temp).getWebName());
                    playerAdded =true;
                    temp = 0;
                    x = x + 1;
                }else{
                    temp = temp + 1;
                }
            }
            playerAdded = false;

        }

    }
    
    public void showInvitePopup(final String sentBy) {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserTeamScreen.this);
            builder.setTitle("Received H2H Invite from: "+sentBy)

                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            replyToInviteRequest(sentBy);
                        }

                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            
                        }

                    });
            builder.show();
        
        
    }

    private void showStats(final Player selectedPlayer,final TextView playerLabel){
        //inflate layout for popup
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.player_stats, null);
        

        String url = "https://platform-static-files.s3.amazonaws.com/premierleague/photos/players/110x140/p"+selectedPlayer.getImageCode()+".png";
        System.out.println(url);
        Bitmap bm = getBitmapFromURL(url);
        
        //set views
        ImageView playerImage = (ImageView)alertLayout.findViewById(R.id.xmlPlayerStatsImage);
        playerImage.setImageBitmap(bm);
        
        TextView playerName = (TextView)alertLayout.findViewById(R.id.xmlPlayerFullNameStats);
        playerName.setText(selectedPlayer.getFirstName()+" "+ selectedPlayer.getSecondName());
        
        
        
        final CharSequence[] cs = subOptions.toArray(new CharSequence[subOptions.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserTeamScreen.this);
        builder.setView(alertLayout);
        
        builder.setTitle("Player stats for: "+selectedPlayer.getWebName())
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });
        builder.show();
    }
    
    private void showSubOptions(final Player selectedPlayer,final TextView playerLabel){
        //inflate layout for popup
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.popup, null);

        //get spinner and set
        final Spinner spinnerPopup = (Spinner) alertLayout.findViewById(R.id.xmlSpinnerPopup);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(alertLayout.getContext(), R.layout.support_simple_spinner_dropdown_item,subOptions);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerPopup.setAdapter(adapter);


        final CharSequence[] cs = subOptions.toArray(new CharSequence[subOptions.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(UserTeamScreen.this);
        builder.setTitle("Pick player")
                .setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Get currennt player in team
                        String playerName = selectedPlayer.getWebName();
                        Player currentPlayer = playerMap.get(playerName);
                        int currentPosInTeam = currentPlayer.getPosInTeam();


                        //get player to sub in
                        playerName = (String) cs[which];
                        Player playerToSwap = playerMap.get(playerName);
                        int playerToSwapPosInTeam = playerToSwap.getPosInTeam();


                        //setIDs
                        currentPlayer.setPosInTeam(playerToSwapPosInTeam);
                        playerToSwap.setPosInTeam(currentPosInTeam);

                        updateLabelFieldsAfterSwap(playerLabel,playerToSwap.getWebName(),playerToSwap);
                        

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });
        builder.show();
    }
    
    private void getSubOptions(Player selectedPlayer){
        //get players you can sub in
        for(Player pl: playersInTeam){
            if(pl.getPlayerPosition() == selectedPlayer.getPlayerPosition()){
                if(pl.getPosInTeam() > 11){
                    subOptions.add(pl.getWebName());
                }
            }
        }
    }
    
    private void updateLabelFieldsAfterSwap(TextView playerLabel, String playerName,Player swappedPlayer){
        playerLabel.setText(playerName);
        if(swappedPlayer.getPosInTeam()-1 >0){
            setImage(swappedPlayer.getTeamCode(),swappedPlayer.getPosInTeam()-1);
        }
        
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

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
