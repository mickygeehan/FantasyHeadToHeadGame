package com.example.michael.fantasyheadtoheadgame;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.michael.fantasyheadtoheadgame.Classes.Game;

import java.util.ArrayList;

public class SpecifiedUserContest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specified_user_contest);

        Intent i = getIntent();
        ArrayList<Game> allGames = (ArrayList<Game>) i.getSerializableExtra("listGames");
        String currentUser = (String)i.getStringExtra("currentUser");
        String opponent = (String)i.getStringExtra("opponent");
        
        initialiseAllFields(currentUser,allGames,opponent);
        
    }
    
    
    private void initialiseAllFields(String currentUser,ArrayList<Game> allGames,String opponent){
        //first set the listView
        initialiseListView(allGames);
        
        //initialise First and second placee
        initialiseTable(currentUser,allGames,opponent);
    }

    private void initialiseTable(String currentUser, ArrayList<Game> allGames,String opponent) {
        int currentUserWins = 0;
        int currentUserDraws = 0;
        int currentUserLosses = 0;

        int opponentWins = 0;
        int opponentDraws = 0;
        int opponentLosses = 0;


        for (Game g : allGames) {
            //then user 1 is current user else user 2 is
            if (g.getUser1().equals(currentUser)) {

                //checks see if current user has won lost or drew
                if (g.getUser1().getPoints() > g.getUser2().getPoints()) {
                    currentUserWins += 1;
                    opponentLosses += 1;
                } else if (g.getUser1().getPoints() < g.getUser2().getPoints()) {
                    opponentWins += 1;
                    currentUserLosses += 1;
                } else {
                    opponentDraws += 1;
                    currentUserDraws += 1;
                }


            } else {

                //checks see if current user has won lost or drew
                if (g.getUser1().getPoints() > g.getUser2().getPoints()) {

                    opponentWins += 1;
                    currentUserLosses += 1;
                } else if (g.getUser1().getPoints() < g.getUser2().getPoints()) {
                    currentUserWins += 1;
                    opponentLosses += 1;
                } else {
                    opponentDraws += 1;
                    currentUserDraws += 1;
                }
            }
            
        }
        
        //Calculate points 
        int currentUserTotalpoints = (currentUserWins * 3) + currentUserDraws;
        int opponentTotalpoints = (opponentWins * 3) + opponentDraws;


        //TextView variables
        TextView firstPlace = (TextView)findViewById(R.id.xmlUserInFirst);
        TextView firstPlaceWins = (TextView)findViewById(R.id.xmlFirstWins);
        TextView firstPlaceDraws = (TextView)findViewById(R.id.xmlFirstDraws);
        TextView firstPlaceLosses = (TextView)findViewById(R.id.xmlFirstLosses);
        TextView firstPlacePoints = (TextView)findViewById(R.id.xmlFirstUserPoints);

        TextView secondPlace = (TextView)findViewById(R.id.xmlUserInSecond);
        TextView secondPlaceWins = (TextView)findViewById(R.id.xmlSecondWins);
        TextView secondPlaceDraws = (TextView)findViewById(R.id.xmlSecondDraws);
        TextView secondPlaceLosses = (TextView)findViewById(R.id.xmlSecondLosses);
        TextView secondPlacePoints = (TextView)findViewById(R.id.xmlSecondUserPoints);
        

        //set text fields
        if(currentUserTotalpoints > opponentTotalpoints){
            firstPlace.setText(currentUser);
            firstPlaceWins.setText(String.valueOf(currentUserWins));
            firstPlaceDraws.setText(String.valueOf(currentUserDraws));
            firstPlaceLosses.setText(String.valueOf(currentUserLosses));
            firstPlacePoints.setText(String.valueOf(currentUserTotalpoints));

            secondPlace.setText(opponent);
            secondPlaceWins.setText(String.valueOf(opponentWins));
            secondPlaceDraws.setText(String.valueOf(opponentDraws));
            secondPlaceLosses.setText(String.valueOf(opponentLosses));
            secondPlacePoints.setText(String.valueOf(opponentTotalpoints));
        }else{

            firstPlace.setText(opponent);
            firstPlaceWins.setText(String.valueOf(opponentWins));
            firstPlaceDraws.setText(String.valueOf(opponentDraws));
            firstPlaceLosses.setText(String.valueOf(opponentLosses));
            firstPlacePoints.setText(String.valueOf(opponentTotalpoints));

            secondPlace.setText(currentUser);
            secondPlaceWins.setText(String.valueOf(currentUserWins));
            secondPlaceDraws.setText(String.valueOf(currentUserDraws));
            secondPlaceLosses.setText(String.valueOf(currentUserLosses));
            secondPlacePoints.setText(String.valueOf(currentUserTotalpoints));
            
        }
        

    }

    private void initialiseListView(ArrayList<Game> allGames){
        ArrayList<String> forListView = new ArrayList<>();
        for(Game g: allGames){
            forListView.add(g.getUser1().getUsername() +" "+g.getUser1().getPoints()+" - "+g.getUser2().getPoints()+" "+g.getUser2().getUsername());
        }

        ListView lv = (ListView)findViewById(R.id.xmlSpecifiedContest);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.mylist,
                forListView);

        lv.setAdapter(arrayAdapter);
    }
}
