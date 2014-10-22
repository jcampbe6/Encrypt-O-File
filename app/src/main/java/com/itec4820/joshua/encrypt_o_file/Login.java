package com.itec4820.joshua.encrypt_o_file;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Joshua on 10/16/2014.
 */
public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //opens file browser
    //should eventually verify login credentials
    public void viewDirectory(View view) {
        Intent intentBrowse = new Intent(this, FileBrowser.class);
        startActivity(intentBrowse);
    }
}
