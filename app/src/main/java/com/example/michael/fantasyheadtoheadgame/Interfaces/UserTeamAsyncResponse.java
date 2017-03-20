package com.example.michael.fantasyheadtoheadgame.Interfaces;

import com.example.michael.fantasyheadtoheadgame.Classes.Player;
import com.example.michael.fantasyheadtoheadgame.Classes.User;

import java.util.ArrayList;

/**
 * Created by michaelgeehan on 14/02/2017.
 */

public interface UserTeamAsyncResponse {
    void processFinish(ArrayList<Player> players);
    void processUserUpdate(String result);
    void processUserMatches(ArrayList<User> users);
    void processLogin(User user);
    void processInvites(String sentBy);
    void processDate(String epochDate);
}
