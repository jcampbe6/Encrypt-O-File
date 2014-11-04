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

public class RegisterActivity extends Activity {

    Button registerButton;
    Button loginLink;
    EditText registrationEmail;
    EditText registrationPassword;
    TextView registrationErrorMessage;

    // JSON Response node names
    private final static String KEY_SUCCESS = "success";
    private final static String KEY_ERROR = "error";
    private final static String KEY_ERROR_MSG = "error_msg";
    private final static String KEY_UID = "uid";
    private final static String KEY_EMAIL = "email";
    private final static String KEY_CREATED_AT = "created_at";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //this thread policy allows network access on main thread -- only used for testing
        //network access should be on separate thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        registerButton = (Button) findViewById(R.id.registerButton);
        loginLink = (Button) findViewById(R.id.loginLink);
        registrationEmail = (EditText) findViewById(R.id.registrationEmail);
        registrationPassword = (EditText) findViewById(R.id.registrationPassword);
        registrationErrorMessage = (TextView) findViewById(R.id.registrationErrorMessage);
    }

    //register new user
    public void register(View view) {
        String email = registrationEmail.getText().toString();
        String password = registrationPassword.getText().toString();
        UserLogRegFunctions loginRegisterHandler = new UserLogRegFunctions();
        JSONObject jsonObj = loginRegisterHandler.registerUser(email, password);

        //check for registration response
        try {
            if (jsonObj.getString(KEY_SUCCESS) != null) {
                registrationErrorMessage.setText("");
                String jsonResponse = jsonObj.getString(KEY_SUCCESS);

                if (Integer.parseInt(jsonResponse) == 1) {
                    //user successfully registered
                    //store user details in SQLite Database
                    DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
                    JSONObject jsonUser = jsonObj.getJSONObject("user");

                    //clear all previous data in database
                    loginRegisterHandler.logoutUser(getApplicationContext());
                    dbHandler.addUser(jsonUser.getString(KEY_EMAIL), jsonObj.getString(KEY_UID), jsonUser.getString(KEY_CREATED_AT));

                    //update registration status in app preferences to true
                    SharedPreferences settings = getSharedPreferences("app_preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("registration_status", true);
                    editor.apply();

                    //launch login screen
                    Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);

                    //close all views before launching login screen
                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentLogin);

                    //close registration screen
                    finish();
                }
                else {
                    //registration error
                    registrationErrorMessage.setText("Error occurred in registration.");
                }
            }
        }
        catch (JSONException e) {
           e.printStackTrace();
        }
    }

    public void goToLogin(View view) {
        //launch login screen
        Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);

        //close all views before launching login screen
        intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentLogin);

        //close registration screen
        finish();
    }
}
