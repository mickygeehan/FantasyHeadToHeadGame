package com.example.michael.fantasyheadtoheadgame.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.LoginHttpRequest;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.TestHomeScreen;
import com.example.michael.fantasyheadtoheadgame.UserHomeScreen.UserHomeScreen;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Login extends Activity  {

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = Login.this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    public void logIn(View view) {
        EditText userName = (EditText) findViewById(R.id.userName);
        EditText userPass = (EditText) findViewById(R.id.password);

        LoginHttpRequest logIn = new LoginHttpRequest(context, userName.getText().toString(), userPass.getText().toString());
        logIn.execute();
        
    }

    public void registerUser(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);

    }

    public static void onBackgroundTaskDataObtained(User userObj) {
        if(userObj != null){
            Intent intent = new Intent(context, TestHomeScreen.class);
            intent.putExtra("UserClass", userObj);
            context.startActivity(intent);
        }else{
            Toast.makeText(context, "I'm sorry your username/password is incorrect!!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
