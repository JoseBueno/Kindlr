package org.bueno.jose.kindlr.utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

/**
 *
 * Created by jbueno on 5/7/2015.
 */
@SuppressWarnings("SameParameterValue")
public class Toaster {
    public static void makeToast(Activity activity, String message) {
        getToast(activity, message, Toast.LENGTH_SHORT, Gravity.CENTER).show();
    }

    public static void makeToast(Activity activity, String message, int duration) {
        getToast(activity, message, duration,  Gravity.CENTER).show();
    }

    public static void makeToast(Activity activity, String message, int duration, int gravity) {
        getToast(activity, message, duration,  gravity).show();
    }

    private  static Toast getToast(Activity activity, String message, int duration,  int gravity) {
        @SuppressLint("ShowToast") Toast toast = Toast.makeText(activity, message, duration);
        toast.setGravity(gravity, toast.getXOffset() /2, toast.getYOffset() /2);
        return toast;
    }
}
