package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.Classes.User;
import com.example.michael.fantasyheadtoheadgame.Constants;
import com.example.michael.fantasyheadtoheadgame.Interfaces.UserTeamAsyncResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by michaelgeehan on 18/02/2017.
 */

public class GetUserMatchesHttpRequest extends AsyncTask<Void, Void, ArrayList<User>> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    Context mainContext;
    ProgressDialog asyncDialog;



    public GetUserMatchesHttpRequest(Context context, int userID, String userName){
        this.mainContext = context;
       this.URL = "http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/GetUserMatches.php?userID="+userID+"&username="+userName;
        System.out.println(URL);
     //   this.URL = "http://10.102.10.146:8888/FantasyShowDown/GetUserMatches.php?userID="+userID+"&username="+userName;
    }


    @Override
    protected ArrayList<User> doInBackground(Void... params) {
        HttpGet request = new HttpGet(URL);
        GetUserMatchesHttpRequest.JSONResponseHandler responseHandler = new GetUserMatchesHttpRequest.JSONResponseHandler();
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
        asyncDialog.setMessage("Searching");
        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<User> users) {

        asyncDialog.dismiss();
        mClient.close();
       // delegate.processFinish(players);

        delegate.processUserMatches(users);

    }



    private class JSONResponseHandler implements ResponseHandler<ArrayList<User>> {


        @Override
        public ArrayList<User> handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            String JSONResponse = new BasicResponseHandler()
                    .handleResponse(response);



            

            ArrayList<User> users = new ArrayList<User>();
            User userObj = null;
            JSONObject obj = null;
            boolean canAdd = true;
            try {
                obj = new JSONObject(JSONResponse);
                JSONArray geodata = obj.getJSONArray("users");
                int n = geodata.length();

                for (int i = 0; i < n; ++i) {
                    canAdd = true;
                    final JSONObject user1 = geodata.getJSONObject(i);
                    userObj = new User(user1.getString("username"),"",0,0,user1.getInt("points"));
                    users.add(userObj);
                    
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return users;
        }


    }

}

