package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.Activities.Register;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class RegisterHttpRequest extends AsyncTask<Void, Void, String> {

    String URL;

    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    Context mainContext;
    ProgressDialog asyncDialog;




    public RegisterHttpRequest(Context context,String userName,String password,String email,String fullName){
        this.mainContext = context;
        //this.URL = "http://192.168.1.105:8888/register.php?userN="+userName+"&password="+password+"&email="+email+"&fullN="+fullName+"";
        this.URL = "http://10.0.2.2:8888/register.php?userN="+userName+"&password="+password+"&email="+email+"&fullN="+fullName+"";
    }



    @Override
    protected String doInBackground(Void... params) {

        HttpGet request = new HttpGet(URL);

        //RegisterHttpRequest.JSONResponseHandler responseHandler = new RegisterHttpRequest.JSONResponseHandler();
        RegisterHttpRequest.JSONResponseHandler responseHandler = new RegisterHttpRequest.JSONResponseHandler();

        try {

            return mClient.execute(request, responseHandler);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPreExecute() {
        asyncDialog = new ProgressDialog(mainContext);
        asyncDialog.setMessage("Registering");
//        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        //System.out.println(result);
        System.out.println("onPost"+result);
      //  asyncDialog.dismiss();
        mClient.close();
        Register.onBackgroundTaskDataObtained(result);


    }



    private class JSONResponseHandler implements ResponseHandler<String> {


        @Override
        public String handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            String JSONResponse = new BasicResponseHandler()
                    .handleResponse(response);


            return JSONResponse;
        }


    }

}
