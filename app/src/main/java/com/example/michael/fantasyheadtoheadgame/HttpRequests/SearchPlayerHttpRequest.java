package com.example.michael.fantasyheadtoheadgame.HttpRequests;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.UtilityClasses.Constants;
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
 * Created by michaelgeehan on 15/02/2017.
 */

public class SearchPlayerHttpRequest extends AsyncTask<Void, Void, ArrayList<Player>> {
    public UserTeamAsyncResponse delegate = null;
    String URL;
    AndroidHttpClient mClient = AndroidHttpClient.newInstance("");

    Context mainContext;
    ProgressDialog asyncDialog;



    public SearchPlayerHttpRequest(Context context, String webName,String firstName){
        this.mainContext = context;
        this.URL = "http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/SearchForPlayer.php?webName="+webName+"&firstName="+firstName;
        //this.URL = "http://10.102.10.146:8888/FantasyShowDown/SearchForPlayer.php?webName="+webName+"&firstName="+firstName;
    }

    public SearchPlayerHttpRequest(Context context, String webName){
        this.mainContext = context;
        this.URL = "http://"+ Constants.IP_ADDRESS+":8888/FantasyShowDown/SearchForPlayer.php?webName="+webName;
       // this.URL = "http://10.102.10.146:8888/FantasyShowDown/SearchForPlayer.php?webName="+webName;
        System.out.println(URL);
    }
    


    @Override
    protected ArrayList<Player> doInBackground(Void... params) {
        HttpGet request = new HttpGet(URL);
        SearchPlayerHttpRequest.JSONResponseHandler responseHandler = new SearchPlayerHttpRequest.JSONResponseHandler();
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
    protected void onPostExecute(ArrayList<Player> players) {

        asyncDialog.dismiss();
        mClient.close();
        delegate.processFinish(players);

    }



    private class JSONResponseHandler implements ResponseHandler<ArrayList<Player>> {


        @Override
        public ArrayList<Player> handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            String JSONResponse = new BasicResponseHandler()
                    .handleResponse(response);

            // JSONResponse = JSONResponse.replace("\\u","");

            ArrayList<Player> players = new ArrayList<Player>();
            Player playerObj = null;
            JSONObject obj = null;
            boolean canAdd = true;
            try {
                obj = new JSONObject(JSONResponse);
                JSONArray geodata = obj.getJSONArray("results");
                int n = geodata.length();

                for (int i = 0; i < n; ++i) {
                    canAdd = true;
                    final JSONObject player = geodata.getJSONObject(i);
                    playerObj = new Player(i+1,player.getString("firstName"),player.getString("secondName"),player.getString("webName"),player.getInt("teamCode"),
                            player.getInt("id"),player.getInt("playerPosition"),player.getDouble("cost"),player.getInt("code"));

                    
                    
                    for(int x=0;x < players.size();x++){
                        if(playerObj.getId() == players.get(x).getId()){
                            canAdd = false;
                            break;
                        }
                    }
                    
                    
                    
                    if(canAdd){
                        players.add(playerObj);
                    }

                    
                    
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return players;
        }


    }

}

