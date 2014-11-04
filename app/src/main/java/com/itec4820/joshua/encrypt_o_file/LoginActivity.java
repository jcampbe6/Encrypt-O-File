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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Joshua on 10/16/2014.
 */
public class LoginActivity extends Activity {
    Button loginButton;
    Button forgotPasswordButton;
    EditText loginEmail;
    EditText loginPassword;
    TextView loginErrorMessage;

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

        /*SharedPreferences settings = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isAppRegistered = settings.getBoolean("registration_status", false);

        Toast.makeText(this, "Registered: " + isAppRegistered, Toast.LENGTH_LONG).show();*/
    }

    //verifies credentials and logs in if successful
    //successful login results in viewing file directory
    //failed login results in error message
    public void login(View view) {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        UserLogRegFunctions logRegFunction = new UserLogRegFunctions();

        //attempts login and returns JSON response
        JSONObject jsonObj = logRegFunction.loginUser(email, password);

        //check for login response
        try {
            String res = jsonObj.getString(KEY_SUCCESS);
            if (res != null) {
                loginErrorMessage.setText("");

                if (Integer.parseInt(res) == 1) {
                    //user successfully logged in
                    //store user details in SQLite Database
                    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                    JSONObject jsonUser = jsonObj.getJSONObject("user");

                    //clear all previous data in database
                    logRegFunction.logoutUser(getApplicationContext());
                    dbHandler.addUser(jsonUser.getString(KEY_EMAIL), jsonObj.getString(KEY_UID), jsonUser.getString(KEY_CREATED_AT));

                    //view directory
                    Intent intentBrowse = new Intent(getApplicationContext(), FileBrowserActivity.class);

                    //close all views before viewing directory
                    intentBrowse.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentBrowse);

                    //close login screen
                    finish();
                }
                else {
                    //login error
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
