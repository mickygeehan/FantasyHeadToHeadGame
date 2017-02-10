package com.example.michael.fantasyheadtoheadgame;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Register extends Activity {
    private static Context context;

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

        //check that passwords are the same
        if(passwordsMatch(password1,password2)){

           // check email is in right format
             //if(emailPatterCheck(email)) {

            //Call a register request
            RegisterHttpRequest reg = new RegisterHttpRequest(context,userName,password1,email,fullName);
            reg.execute();

           //  }else{

           // }

        }else{
            Toast.makeText(Register.this,"Password's do not match!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean passwordsMatch(String pass1,String pass2){
        if(pass1.equals(pass2)){
            return true;
        }
        return false;
    }

    private boolean emailPatterCheck(String email){

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    //Data back from RegisterHttpRequest
    public static void onBackgroundTaskDataObtained(String result) {
        Toast.makeText(context,result,
                Toast.LENGTH_LONG).show();


    }
}
