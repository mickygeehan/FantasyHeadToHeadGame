package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.Constants;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by michaelgeehan on 01/03/2017.
 */

public class GetBudgetHTTPRequest extends AsyncTask<Object, Object, String> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    private Context mainContext;
    private ProgressDialog asyncDialog;



    public GetBudgetHTTPRequest(Context context,int userID){
        this.mainContext = context;
        this.URL = "http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/GetBudget.php?userID="+userID;
        //this.URL = "http://10.102.10.146:8888/FantasyShowDown/GetBudget.php?userID="+userID;
        System.out.println(this.URL);
    }


    @Override
    protected String doInBackground(Object... params) {
        HttpGet request = new HttpGet(URL);
        GetBudgetHTTPRequest.JSONResponseHandler responseHandler = new GetBudgetHTTPRequest.JSONResponseHandler();
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

    }

    @Override
    protected void onPostExecute(String budget) {
        mClient.close();
        delegate.processUserUpdate(budget);

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
