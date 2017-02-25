package com.example.michael.fantasyheadtoheadgame.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michael.fantasyheadtoheadgame.HttpRequests.RegisterHttpRequest;
import com.example.michael.fantasyheadtoheadgame.R;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Register extends Activity {
    private static Context context;
    
    //Error labels
    private final String userNameEmpty = "Please enter a username!";
    private final String passwordEmpty = "Please enter a password!";
    private final String emailEmpty = "Please enter an email!";
    private final String fullNameEmpty = "Please enter a name!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        context = Register.this;
    }

    public void register(View view){
        EditText userNameEditText = (EditText)findViewById(R.id.regUsername);
        EditText emailEditText = (EditText)findViewById(R.id.regEmail);
        EditText fullNameEditText = (EditText)findViewById(R.id.regFullName);
        EditText password1EditText = (EditText)findViewById(R.id.regPassword1);
        EditText password2EditText = (EditText)findViewById(R.id.regPassword2);

        String userName = userNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString();
        String password1 = password1EditText.getText().toString();
        String password2 = password2EditText.getText().toString();
        
        if(checkFields(userName,email,fullName,password1,password2)){
            if(passwordsMatch(password1,password2)){
                RegisterHttpRequest reg = new RegisterHttpRequest(context,userName,password1,email,fullName);
                reg.execute();
            }else{
                displayToast("Password's do not match!");
            }
        }

    }
    
    private boolean checkFields(String userName,String email,String fullName,String password1,String password2){
        if(userName.isEmpty()){
            displayToast(userNameEmpty);
            return false;
        }else if(email.isEmpty()){
            displayToast(emailEmpty);
            return false;
        }else if(fullName.isEmpty()){
            displayToast(fullNameEmpty);
            return false;
        }else if(password1.isEmpty()){
            displayToast(passwordEmpty);
            return false;
        }else if(password2.isEmpty()){
            displayToast(passwordEmpty);
            return false;
        }
        return true;
    }

    private void displayToast(String message){
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }
    
    public void gotoLogin(View view){
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        finish();
    }
    
    
    private boolean passwordsMatch(String pass1,String pass2){
        return pass1.equals(pass2);
    }

    //Data back from RegisterHttpRequest
    public static void onBackgroundTaskDataObtained(String result) {
        if(result == null){
            Toast.makeText(context,"Server Issue",
                    Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(context,result,
                    Toast.LENGTH_LONG).show();
        }
        


    }
}
