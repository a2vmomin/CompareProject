package com.example.alif.compareproject2;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Alif on 15-Jul-15.
 */
public class Message {
    public static void message(Context context, String message)
    {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
