package com.itec4820.joshua.encrypt_o_file;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Joshua on 10/9/2014.
 */
public class MainActivity extends ActionBarActivity {

    private boolean registered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (registered) {
            //switching to login screen
            Intent intentLogin = new Intent(getApplicationContext(), Login.class);
            startActivity(intentLogin);
            finish();
        }
        else {
            setContentView(R.layout.activity_main);
            Button registerLink = (Button)findViewById(R.id.registrationLink);

            //listening for register link click
            registerLink.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Switching to Register screen
                    Intent intentRegister = new Intent(getApplicationContext(), Register.class);
                    startActivity(intentRegister);
                    finish();
                }
            });
        }
    }
}
