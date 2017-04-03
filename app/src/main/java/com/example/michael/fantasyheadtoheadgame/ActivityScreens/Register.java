package com.example.michael.fantasyheadtoheadgame.ActivityScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.michael.fantasyheadtoheadgame.Classes.MySingleton;
import com.example.michael.fantasyheadtoheadgame.Classes.RequestResponseParser;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.CommonUtilityMethods;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;
import com.example.michael.fantasyheadtoheadgame.HttpRequests.RegisterHttpRequest;
import com.example.michael.fantasyheadtoheadgame.R;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.SecurityMethods;


/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class Register extends Activity {

    //Volley variables
    private RequestQueue queue;
    private RequestResponseParser responseParser;
    private ProgressDialog progressD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //initialise request
        queue = MySingleton.getInstance(this.getApplicationContext()).
                getRequestQueue();

        if(!initialiseParser()){
            responseParser = new RequestResponseParser();
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
    
    private boolean registerUserRequest(String appendToUrl){
        String url =Constants.REGISTER_ADDRESS+appendToUrl;
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
    
    public void register(View view){
        String appendToUrl = "";
        EditText userNameEditText = (EditText)findViewById(R.id.regUsername);
        EditText emailEditText = (EditText)findViewById(R.id.regEmail);
        EditText fullNameEditText = (EditText)findViewById(R.id.regFullName);
        EditText password1EditText = (EditText)findViewById(R.id.regPassword1);
        EditText password2EditText = (EditText)findViewById(R.id.regPassword2);

        String userName = userNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String fullName = fullNameEditText.getText().toString().replace(" ","+");
        String password1 = password1EditText.getText().toString();
        String password2 = password2EditText.getText().toString();
        
        //checks that all input is correct
        //check not empty + check for sql injection prevention
        if(checkFieldsEmpty(userName,email,fullName,password1,password2)){
            if(SecurityMethods.isCleanInput(userName)){
                    if(SecurityMethods.isCleanInput(fullName)) {

                        if (passwordsMatch(password1, password2)) {
                            appendToUrl = "userN="+userName+"&password="+SecurityMethods.hashPassword(password1)+"&email="+email+"&fullN="+fullName;
                            
                            registerUserRequest(appendToUrl);
                            
                        } else {
                            CommonUtilityMethods.displayToast(getApplicationContext(),"Password's do not match!");
                        }


                    }
            }
            
        }

    }
    
    private boolean checkFieldsEmpty(String userName,String email,String fullName,String password1,String password2){
        String message = "";
        if(userName.isEmpty()){
            message = Constants.USERNAME_EMPTY;
        }else if(email.isEmpty()){
            message = Constants.EMAIL_EMPTY;
        }else if(fullName.isEmpty()){
            message = Constants.FULLNAME_EMPTY;
        }else if(password1.isEmpty()){
            message = Constants.PASSWORD_EMPTY;
        }else if(password2.isEmpty()){
            message = Constants.PASSWORD_EMPTY;
        }
        if(message.isEmpty()){
            return true;
        }else{
            CommonUtilityMethods.displayToast(getApplicationContext(),message);
            return false;
        }
        
    }
    
    public void gotoLogin(View view){
        Intent i = new Intent(this,Login.class);
        startActivity(i);
        finish();
    }
    
    private boolean passwordsMatch(String pass1,String pass2){
        return pass1.equals(pass2);
    }

//    //Data back from RegisterHttpRequest
//    public static void onBackgroundTaskDataObtained(String result) {
//        if(result == null){
//            Toast.makeText(context,"Server Issue",
//                    Toast.LENGTH_LONG).show();
//        }else{
//            Toast.makeText(context,result,
//                    Toast.LENGTH_LONG).show();
//        }
//        
//
//
//    }
}
