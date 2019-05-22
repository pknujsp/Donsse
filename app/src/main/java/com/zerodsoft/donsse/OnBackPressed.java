package com.zerodsoft.donsse;

import android.app.Activity;
import android.widget.Toast;

public class OnBackPressed
{
    Activity activity;
    long pressedtime = 0;
    String toastmessage;

    public OnBackPressed(Activity ACTIVITY, String message)
    {
        this.activity = ACTIVITY;
        this.toastmessage = message;
    }

    public boolean BackPressed()
    {
        if (System.currentTimeMillis() > pressedtime + 2000)
        {
            pressedtime = System.currentTimeMillis();
            Toast.makeText(activity, toastmessage, Toast.LENGTH_LONG).show();
            return false;
        } else
        {
            activity.finish();
            return true;
        }
    }
}
