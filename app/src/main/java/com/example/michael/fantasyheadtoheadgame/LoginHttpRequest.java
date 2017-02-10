package com.example.michael.fantasyheadtoheadgame;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michaelgeehan on 10/02/2017.
 */

public class LoginHttpRequest extends AsyncTask<Void, Void, String> {

    //String URL = "http://192.168.1.105:8888/register.php?userN=hjjhgewrtrtrtiigihuhuhuoohu&password=hi&email=jim@gmail.com&fullN=djdjd";

    //String URL = "http://192.168.1.105:8888/register.php?userN=ComingFromAndroidPhone&password=hi&email=jim@gmail.com&fullN=djdjd";
    String URL;

    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    Context mainContext;
    ProgressDialog asyncDialog;

    //user details
    String userName,userPass;



    public LoginHttpRequest(Context context,String username,String password){
        this.mainContext = context;
        this.userName = username;
        this.userPass = password;
        this.URL = addLoginToUrl("http://10.0.2.2:8888/login.php",username,password);
        // this.URL = "http://192.168.1.105:8888/login.php?userN="+username+"&password="+password+"";
    }

    protected String addLoginToUrl(String url,String userName,String password){
        if(!url.endsWith("?")){
            url += "?";
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("userN",userName));
            params.add(new BasicNameValuePair("password",password));
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += paramString;
        }

        return url;

    }



    @Override
    protected String doInBackground(Void... params) {
        HttpGet request = new HttpGet(URL);
        //RegisterHttpRequest.JSONResponseHandler responseHandler = new RegisterHttpRequest.JSONResponseHandler();
        LoginHttpRequest.JSONResponseHandler responseHandler = new LoginHttpRequest.JSONResponseHandler();

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
        asyncDialog.setMessage("Logging In");
        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        //System.out.println(result);
        System.out.println("onPost"+result);
        asyncDialog.dismiss();
        mClient.close();
        Login.onBackgroundTaskDataObtained(result);

    }



    private class JSONResponseHandler implements ResponseHandler<String> {


        @Override
        public String handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            String JSONResponse = new BasicResponseHandler()
                    .handleResponse(response);

            System.out.println(JSONResponse);


            return JSONResponse;
        }


    }

}
