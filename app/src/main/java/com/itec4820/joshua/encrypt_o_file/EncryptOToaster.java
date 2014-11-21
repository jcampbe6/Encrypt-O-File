package com.itec4820.joshua.encrypt_o_file;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Joshua on 11/20/2014.
 */
public class EncryptOToaster {
    private static Toast toast;

    public static Toast makeText(Activity activity, String message, int duration) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(message);

        toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(layout);

        return toast;
    }

    public static void show() {
        toast.show();
    }
}
