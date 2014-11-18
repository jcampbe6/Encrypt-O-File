package com.itec4820.joshua.encrypt_o_file;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class: LoginActivity
 * Created by Joshua on 10/16/2014.
 *
 * Purpose: To attempt to login a user in order to use the Encrypt-O-File app. Provides
 * the user with a login interface. If the user provides correct credentials, access is granted,
 * otherwise an error message is displayed.
 */
public class LoginActivity extends Activity {
    Button forgotPasswordButton;
    Button loginButton;
    EditText loginEmail;
    EditText loginPassword;
    TextView loginErrorMessage;
    CheckBox rememberMeCheckBox;
    boolean isAppRegistered;
    boolean rememberMe;

    // JSON Response node names
    private final static String KEY_SUCCESS = "success";
    private final static String KEY_ERROR = "error";
    private final static String KEY_ERROR_MSG = "error_msg";
    private final static String KEY_UID = "uid";
    private final static String KEY_EMAIL = "email";
    private final static String KEY_CREATED_AT = "created_at";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //this thread policy allows network access on main thread -- only used for testing
        //network access should be on separate thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        loginButton = (Button) findViewById(R.id.loginButton);
        forgotPasswordButton = (Button) findViewById(R.id.forgotPasswordButton);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        loginErrorMessage = (TextView) findViewById(R.id.loginErrorMessage);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);

        // retrieves 'remember me' preference setting
        SharedPreferences settings = getSharedPreferences("app_preferences", MODE_PRIVATE);
        rememberMe = settings.getBoolean("remember_me", false);
        if (rememberMe) {
            loginEmail.setText(settings.getString("email", ""));
            rememberMeCheckBox.setChecked(true);
            //loginPassword.setFocusableInTouchMode(true);
            //loginPassword.requestFocus();
            loginPassword.setText(settings.getString("password", ""));
            //loginButton.setFocusable(true);
            //loginButton.requestFocus();
        }
    }

    /**
     * Method: login
     * @param view
     * Attempts to log user in by verifying credentials provided by user against the user database
     * on an external server. A successful login results in user being granted access to browse
     * phone directory. A failed login results in an error message being displayed.
     */
    public void login(View view) {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        UserLogRegFunctions logRegFunction = new UserLogRegFunctions();

        //attempts login and returns JSON response
        JSONObject jsonObj = logRegFunction.loginUser(email, password);

        //check for 'success' response
        try {
            String response = jsonObj.getString(KEY_SUCCESS);
            if (response != null) {
                loginErrorMessage.setText("");

                //if 'success' response is '1', user successfully logged in
                if (Integer.parseInt(response) == 1) {
                    //store user details in SQLite Database
                    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                    JSONObject jsonUser = jsonObj.getJSONObject("user");

                    //clear all previous data in database
                    logRegFunction.logoutUser(getApplicationContext());
                    dbHandler.addUser(jsonUser.getString(KEY_EMAIL), jsonObj.getString(KEY_UID), jsonUser.getString(KEY_CREATED_AT));

                    //get preferences
                    SharedPreferences settings = getSharedPreferences("app_preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();

                    //if app isn't already registered, update registration status in app
                    //preferences to true
                    isAppRegistered = settings.getBoolean("registration_status", false);
                    if (!isAppRegistered) {
                        editor.putBoolean("registration_status", true);
                        editor.apply();
                    }

                    //set preferences to remember login email based on checked status of checkbox
                    if (rememberMeCheckBox.isChecked()) {
                        editor.putBoolean("remember_me", true);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();
                    }
                    else {
                        editor.putBoolean("remember_me", false);
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.apply();
                    }

                    //start intent to browse directory
                    Intent intentBrowse = new Intent(getApplicationContext(), FileBrowserActivity.class);

                    //close all views before viewing directory
                    intentBrowse.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBrowse);

                    //close login screen
                    finish();
                }
                //else if login failed, display login error message
                else {
                    loginErrorMessage.setText("Incorrect email/password");
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void resetPassword(View view) {
        Toast.makeText(getApplicationContext(), "No Functionality Yet", Toast.LENGTH_SHORT).show();
    }
}
