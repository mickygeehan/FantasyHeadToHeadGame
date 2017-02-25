package com.example.michael.fantasyheadtoheadgame.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.LoginHttpRequest;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;
import com.example.michael.fantasyheadtoheadgame.MainHub;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.SessionManager;

import java.util.ArrayList;


/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Login extends Activity implements UserTeamAsyncResponse {

    private static Context context;
    SessionManager session;
    
    //Error Labels
    private final String userNameEmpty = "Please enter a username!";
    private final String passwordEmpty = "Please enter a password!";
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.login);
        
        context = Login.this;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        this.finish();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
    
    public void logIn(View view) {
        EditText userName = (EditText) findViewById(R.id.userName);
        EditText userPass = (EditText) findViewById(R.id.password);
        
        String userN = userName.getText().toString();
        String pass = userPass.getText().toString();
        
        System.out.println(pass);
        
        //Check username and password is entered
        if(!userN.isEmpty() && !pass.isEmpty()){
            LoginHttpRequest logIn = new LoginHttpRequest(context, userName.getText().toString(), userPass.getText().toString());
            logIn.delegate = this;
            logIn.execute();
            
        }else{
            if(userN.isEmpty()){
                displayToast(userNameEmpty);
            }else{
                displayToast(passwordEmpty);
            }
        }
        
    }
    
    private void displayToast(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }
    
    public void registerUser(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);

    }


    @Override
    public void processLogin(User user) {
        if(user != null){
            session = new SessionManager(getApplicationContext());
            session.createLoginSession(user.getUsername(),user.getEmail(),user.getId());
            Intent intent = new Intent(context, MainHub.class);
            intent.putExtra("UserClass", user);
            context.startActivity(intent);
            finishAffinity();
            

        }else{
            Toast.makeText(context, "I'm sorry your username/password is incorrect!!",
                    Toast.LENGTH_LONG).show();
        }
    }
    

    @Override
    public void processFinish(ArrayList<Player> players) {
        
    }

    @Override
    public void processUserUpdate(String result) {

    }

    @Override
    public void processUserMatches(ArrayList<User> users) {

    }

    
}
