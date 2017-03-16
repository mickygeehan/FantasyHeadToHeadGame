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
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by michaelgeehan on 15/02/2017.
 */

public class UpdateUserTeamHttpResponse extends AsyncTask<Object, Object, String> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    private Context mainContext;
    private ProgressDialog asyncDialog; 



    public UpdateUserTeamHttpResponse(Context context,String urlToAdd){
        this.mainContext = context;
        this.URL = "http://10.0.2.2:8888/FantasyShowDown/UpdateUserTeam.php"+urlToAdd;
       //this.URL = "http://10.102.10.146:8888/FantasyShowDown/UpdateUserTeam.php"+urlToAdd;
        System.out.println(URL);
    }


    @Override
    protected String doInBackground(Object... params) {
        HttpGet request = new HttpGet(URL);
        UpdateUserTeamHttpResponse.JSONResponseHandler responseHandler = new UpdateUserTeamHttpResponse.JSONResponseHandler();
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
        asyncDialog.setMessage("Updating team");
        asyncDialog.show();
    }

    @Override
    protected void onPostExecute(String updated) {

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
            

            JSONObject obj = null;
//            try {
//                obj = new JSONObject(JSONResponse);
//                JSONArray geodata = obj.getJSONArray("results");
//                int n = geodata.length();
//
//                for (int i = 0; i < n; ++i) {
//                    final JSONObject player = geodata.getJSONObject(i);
//                 //  playerObj = new Player(i+1,player.getString("firstName"),player.getString("secondName"),player.getString("webName"),player.getInt("teamCode"),
//                       //     player.getInt("id"),player.getInt("playerPosition"),player.getDouble("cost"));
//
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


            return "";
        }


    }

}

