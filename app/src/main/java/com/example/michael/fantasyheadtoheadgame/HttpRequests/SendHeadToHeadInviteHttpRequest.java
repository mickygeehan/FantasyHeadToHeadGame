package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;

/**
 * Created by michaelgeehan on 18/03/2017.
 */

public class SendHeadToHeadInviteHttpRequest extends AsyncTask<Object, Object, String> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    private Context mainContext;
    private ProgressDialog asyncDialog;



    public SendHeadToHeadInviteHttpRequest(Context context,int userID,String userName,String userToInvite){
        this.mainContext = context;
        this.URL = "http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/FindSpecifiedHeadToHeadMatch.php?fromUsername="+userName+"&userID="+userID+"&toUsername="+userToInvite;
        //this.URL = "http://10.102.10.146:8888/FantasyShowDown/FindHeadToHeadMatch.php?username="+userName+"&userID="+userID;
        System.out.println(this.URL);
    }


    @Override
    protected String doInBackground(Object... params) {
        HttpGet request = new HttpGet(URL);
        SendHeadToHeadInviteHttpRequest.JSONResponseHandler responseHandler = new SendHeadToHeadInviteHttpRequest.JSONResponseHandler();
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
        asyncDialog.setMessage("Inviting Player");
        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(String updated) {
        System.out.println(updated);
        asyncDialog.dismiss();
        mClient.close();
        delegate.processUserUpdate(updated);

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