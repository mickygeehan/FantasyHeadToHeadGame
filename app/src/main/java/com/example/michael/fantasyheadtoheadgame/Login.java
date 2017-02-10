package com.example.michael.fantasyheadtoheadgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Login extends Activity {

    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = Login.this;
    }


    public void logIn(View view) {
        EditText userName = (EditText) findViewById(R.id.userName);
        EditText userPass = (EditText) findViewById(R.id.password);
        // Intent intent = new Intent(LogIn.this, MainActivity.class);
        //intent.putExtra("USERNAME", userName.getText().toString());
        // startActivity(intent);
        LoginHttpRequest logIn = new LoginHttpRequest(context, userName.getText().toString(), userPass.getText().toString());
        logIn.execute();

    }

    public void registerUser(View view) {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);

    }

    public static void onBackgroundTaskDataObtained(String result) {
        Toast.makeText(context, result,
                Toast.LENGTH_LONG).show();

    }
}
