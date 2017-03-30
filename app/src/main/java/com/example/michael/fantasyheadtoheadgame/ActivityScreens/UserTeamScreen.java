package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.FindHeadToHeadMatchHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.FindInvitesHttprequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetBudgetHTTPRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.GetUserTeamHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.ReplyToInviteHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.SendHeadToHeadInviteHttpRequest;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.UpdateUserTeamHttpResponse;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.R;

import java.util.ArrayList;
import java.util.HashMap;

public class UserTeamScreen extends AppCompatActivity implements UserTeamAsyncResponse {
    
    //User class
    private User u;
    //user players
    private ArrayList<Player> playersInTeam;
    private HashMap<String, Player> playerMap;
    private ArrayList<String> playersNames;
    private ArrayList<String> subOptions;
    //num of players in pos
    private int numDef,numMid,numAtt = 0;
    private int budget = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Saved your team", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                updateUserTeamInDB(u.getBudget());
            }
        });
        
        //get user
        User user = (User) getIntent().getSerializableExtra("UserClass");
        u = user;
        
        //getSupportActionBar().setTitle("Your Team \t\tBudget: "+u.getBudget());
        getHeadToHeadInvites(u.getUsername());
        getBudget();
        callGetUserTeam(u.getId());

    }
    
    private void getHeadToHeadInvites(String uName){
        //checks if you have any head to head invites
        FindInvitesHttprequest findIn = new FindInvitesHttprequest(this,u.getId(),u.getUsername());
        findIn.delegate = this;
        findIn.execute();
    }
    
    
    private void getBudget(){
        GetBudgetHTTPRequest getBudget = new GetBudgetHTTPRequest(this,u.getId());
        getBudget.delegate = this;
        getBudget.execute();
    }
    
    @Override
    protected void onRestart() {
        super.onRestart();
        
        callGetUserTeam(u.getId());
        
        

        
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        
    }

    private void sortPlayerNames(){
        //sort them

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
    
    private void callGetUserTeam(int userID){
        GetUserTeamHttpRequest getWeeklyTeam = new GetUserTeamHttpRequest(this,userID);
        getWeeklyTeam.delegate = this;
        getWeeklyTeam.execute();
        
    }

    public void openSearchScreen(View view){
        Intent intent = new Intent(UserTeamScreen.this, SearchPlayers.class);
        intent.putExtra("numberPlayers", playersInTeam.size());
        intent.putExtra("userID", u.getId());
        intent.putExtra("budget", u.getBudget());
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                int updateBudget=data.getIntExtra("budget",0);
                u.setBudget(updateBudget);
                updateUserTeamInDB(u.getBudget());
                getSupportActionBar().setTitle("Your Team \t\tBudget: "+u.getBudget());
            }
        }
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
                        SendHeadToHeadInviteHttpRequest sendH2HInvite = new SendHeadToHeadInviteHttpRequest(UserTeamScreen.this,u.getId(),u.getUsername(),input.getText().toString());
                        sendH2HInvite.delegate = UserTeamScreen.this;
                        sendH2HInvite.execute();
                    }

                })
                .setPositiveButton("Random", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FindHeadToHeadMatchHttpRequest findH2H = new FindHeadToHeadMatchHttpRequest(UserTeamScreen.this,u.getId(),u.getUsername());
                        findH2H.delegate = UserTeamScreen.this;
                        findH2H.execute();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }

                });
        builder.show();
        
        
        
        
        
        
        
        
        
        
        
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
    
    @Override
    public void processFinish(ArrayList<Player> players) {
        //initialise variables
        playersInTeam = players;
        playersNames = new ArrayList<>();
        playerMap = new HashMap<>();
        subOptions = new ArrayList<>();

        for(Player p: players){
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
        
        initialiseFields();
        
    }

    @Override
    public void processUserUpdate(String result) {
        if(result != ""){
            try {
                budget = Integer.valueOf(result);
                u.setBudget(budget);
                getSupportActionBar().setTitle("Your Team \t\tBudget: "+budget);
            } catch(NumberFormatException e) {
                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            } catch(NullPointerException e) {
                
            }
        }
       
        
    }

    @Override
    public void processUserMatches(ArrayList<User> users) {
        
    }

    @Override
    public void processLogin(User user) {
        
    }

    @Override
    public void processInvites(final String sentBy) {
        if(sentBy.contains("You have no")){
            Toast.makeText(this,sentBy,Toast.LENGTH_LONG).show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(UserTeamScreen.this);
            builder.setTitle("Received H2H Invite from: "+sentBy)

                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ReplyToInviteHttpRequest replyH2H = new ReplyToInviteHttpRequest(UserTeamScreen.this,u.getId(),sentBy,u.getUsername());
                            replyH2H.delegate = UserTeamScreen.this;
                            replyH2H.execute();
                        }

                    })
                    .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }

                    });
            builder.show();
        }
        
    }

    @Override
    public void processDate(String epochDate) {
        Long epoch = Long.getLong(epochDate);
        
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
        displayPopUp(selectedPlayer,playerLabel);
        
    }
    
    private void displayPopUp(final Player selectedPlayer,final TextView playerLabel){
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
                        
                        
                        //ystem.out.println(currentPosInTeam+"-"+playerToSwapPosInTeam);


                        //setIDs
                        currentPlayer.setPosInTeam(playerToSwapPosInTeam);
                        playerToSwap.setPosInTeam(currentPosInTeam);

                        updateLabelFieldsAfterSwap(playerLabel,playerToSwap.getWebName(),playerToSwap);


                        Toast.makeText(getApplicationContext(),"You have now subbed in this player!",
                                Toast.LENGTH_LONG).show();



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

    public void updateUserTeamInDB(int budget){
        
        sortPlayerNames();
        String url = "?";
        int i = 1;

        for(String name : playersNames){
            Player pl = playerMap.get(name);
            url = url + "player"+i+"="+pl.getId()+"&";
            i++;
        }
        url = url+"userID="+u.getId()+"&budget="+budget;

        UpdateUserTeamHttpResponse getWeeklyTeam = new UpdateUserTeamHttpResponse(UserTeamScreen.this,url);
        getWeeklyTeam.delegate = this;
        getWeeklyTeam.execute();
    }
}
