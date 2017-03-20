package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by michaelgeehan on 19/03/2017.
 */

public class GetDeadlineHttpRequest extends AsyncTask<Object, Object, String> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    private Context mainContext;
    private ProgressDialog asyncDialog;



    public GetDeadlineHttpRequest(Context context){
        this.mainContext = context;
        this.URL = "http://10.0.2.2:8888/FantasyShowDown/GetDeadline.php";
        //this.URL = "http://10.102.10.146:8888/FantasyShowDown/FindHeadToHeadMatch.php?username="+userName+"&userID="+userID;
        System.out.println(this.URL);
    }


    @Override
    protected String doInBackground(Object... params) {
        HttpGet request = new HttpGet(URL);
        GetDeadlineHttpRequest.JSONResponseHandler responseHandler = new GetDeadlineHttpRequest.JSONResponseHandler();
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
        asyncDialog.setMessage("Loading...");
        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(String updated) {
        System.out.println(updated);
        asyncDialog.dismiss();
        mClient.close();
        delegate.processDate(updated);

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