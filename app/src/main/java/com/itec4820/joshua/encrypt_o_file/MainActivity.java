package com.itec4820.joshua.encrypt_o_file;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Class: MainActivity
 * Created by Joshua on 10/9/2014.
 *
 * Purpose: This is the main/initial activity that is opened when the Encrypt-O-File app is first
 * launched. This activity determines whether to proceed to a login screen or a registration
 * screen based on whether or not the app has already been registered.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get preferences
        SharedPreferences settings = getSharedPreferences("app_preferences", MODE_PRIVATE);
        boolean isAppRegistered = settings.getBoolean("registration_status", false);

        //if app is registered, proceed to login screen
        if (isAppRegistered) {
            //switching to login screen
            Intent intentLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intentLogin);
            finish();
        }
        //if app isn't registered, provide registration link
        else {
            setContentView(R.layout.activity_main);
            Button registerLink = (Button)findViewById(R.id.registrationLink);

            //listening for register link click -- if link is clicked, proceed to
            //registration screen
            registerLink.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Switching to Register screen
                    Intent intentRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intentRegister);
                    finish();
                }
            });
        }
    }
}
