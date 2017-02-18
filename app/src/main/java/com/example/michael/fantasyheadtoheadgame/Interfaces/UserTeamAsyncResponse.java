package com.example.michael.fantasyheadtoheadgame.Interfaces;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;

import java.util.ArrayList;

/**
 * Created by michaelgeehan on 14/02/2017.
 */

public interface UserTeamAsyncResponse {
    void processFinish(ArrayList<Player> players);
    void processUserUpdate(String result);
}
