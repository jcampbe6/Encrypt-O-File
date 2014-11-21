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

    private static final String LOGIN_URL = "http://54.173.40.64/";
    private static final String REGISTER_URL = "http://54.173.40.64/";

    private static final String LOGIN_TAG = "login";
    private static final String REGISTER_TAG = "register";

    // constructor
    public UserLogRegFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * Method: loginUser
     * @param email the user's registered email
     * @param password the user's registered password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", LOGIN_TAG));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // return JSON Object
        return jsonParser.getJSONFromUrl(LOGIN_URL, params);
    }

    /**
     * Method: registerUser
     * @param email the user's email
     * @param password the user's chosen password
     * */
    public JSONObject registerUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", REGISTER_TAG));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // return JSON Object
        return jsonParser.getJSONFromUrl(REGISTER_URL, params);
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
