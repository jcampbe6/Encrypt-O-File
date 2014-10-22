package com.itec4820.joshua.encrypt_o_file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class Register extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView loginLink = (TextView) findViewById(R.id.loginLink);

        //listening for login link click
        loginLink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //Switching to Login screen
                Intent intentLogin = new Intent(getApplicationContext(), Login.class);
                startActivity(intentLogin);
            }
        });
    }
}