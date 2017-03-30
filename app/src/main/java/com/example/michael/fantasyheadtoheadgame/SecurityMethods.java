package com.example.michael.fantasyheadtoheadgame;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by michaelgeehan on 30/03/2017.
 */

public class SecurityMethods{
    
    //Prevents sql injection
    public static boolean isCleanInput(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        return s.matches(pattern);
    }

    public static String hashPassword(String password){
        final MessageDigest digest;
        String hashedPass = "";
        byte[] result;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            result = digest.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();

            for (byte b : result) // This is your byte[] result..
            {
                sb.append(String.format("%02X", b));
            }
            hashedPass = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }




        return hashedPass;




    }
}
