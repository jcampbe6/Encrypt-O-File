package com.itec4820.joshua.encrypt_o_file;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import javax.crypto.BadPaddingException;

/**
 * Created by Joshua on 11/19/2014.
 */
public abstract class EncryptorTask extends AsyncTask<Void, Void, String> {
    Exception error;
    Context context;

    public EncryptorTask(Context context){
        this.context = context;
    }

    protected abstract String doEncryptorTask();

    @Override
    protected String doInBackground(Void... params) {
        try {
            return doEncryptorTask();
        }
        catch (Exception e) {
            error = e;
            Log.e("EncryptorTask: ", e.getMessage(), e);
            return null;
        }
    }

    protected abstract void updateUI(String filePath);

    @Override
    protected void onPostExecute(String filePath) {
        if (error != null) {
            Toast.makeText(context, "Incorrect Password", Toast.LENGTH_LONG).show();
            return;
        }

        updateUI(filePath);
    }
}
