package ru.malyshev.cranescale;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Developer on 04.02.18.
 */

public class SupportClass {

    public static void ToastMessage(Context context, String value){
        Toast toast = Toast.makeText(context, value, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String checkStringToNullAndTrim(String value){
        if(value != null){
            return value.trim();
        }else{
            return "";
        }
    }
}