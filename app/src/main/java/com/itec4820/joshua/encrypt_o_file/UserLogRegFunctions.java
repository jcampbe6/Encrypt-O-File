package com.itec4820.joshua.encrypt_o_file;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

/**
 * Created by Joshua on 10/25/2014.
 */
public class UserLogRegFunctions {
    private JSONParser jsonParser;

    // Testing in localhost using wamp or xampp
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://54.173.40.64/";
    private static String registerURL = "http://54.173.40.64/";

    private static String loginTag = "login";
    private static String registerTag = "register";

    // constructor
    public UserLogRegFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", loginTag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // return JSON Object
        return jsonParser.getJSONFromUrl(loginURL, params);
    }

    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", registerTag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // return JSON Object
        return jsonParser.getJSONFromUrl(registerURL, params);
    }

    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        int count = dbHandler.getRowCount();
        return count > 0;
    }

    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        DatabaseHandler dbHandler = new DatabaseHandler(context);
        dbHandler.resetTables();
        return true;
    }

}
