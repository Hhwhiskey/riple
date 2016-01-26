package com.khfire22.riple.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by Kevin on 1/11/2016.
 */
/*
Add haptic feedback to all ling presses
 */
public class Vibrate {

    public void vibrate(Context context) {
        Vibrator vb = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vb.vibrate(10);
    }
}
