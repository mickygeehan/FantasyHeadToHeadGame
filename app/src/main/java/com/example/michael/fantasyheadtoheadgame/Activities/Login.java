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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            String passwordHashed = hashPassword(pass);
            LoginHttpRequest logIn = new LoginHttpRequest(context, userName.getText().toString(), passwordHashed);
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
    
    private String hashPassword(String password){
        final MessageDigest digest;
        String toReturn = "";
        byte[] result;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            result = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (byte b : result) // This is your byte[] result..
            {
                sb.append(String.format("%02X", b));
            }
            toReturn = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        
        
        return toReturn;

        

        
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
    public void processInvites(String sentBy) {
        
    }

    @Override
    public void processDate(String epochDate) {
        
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
