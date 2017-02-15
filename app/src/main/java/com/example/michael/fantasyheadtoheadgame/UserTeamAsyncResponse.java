package com.example.michael.fantasyheadtoheadgame;

import java.util.ArrayList;

/**
 * Created by michaelgeehan on 14/02/2017.
 */

public interface UserTeamAsyncResponse {
    void processFinish(ArrayList<Player> players);
    void processUserUpdate(String result);
}
