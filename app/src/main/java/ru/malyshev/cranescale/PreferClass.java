package ru.malyshev.cranescale;

//Created by Developer on 05.02.18.

import android.content.Context;
import android.content.SharedPreferences;

public class PreferClass {

    public static final String PREFERENCE_CS = "preference_crane_scale";
    public static final String CS_RESULTS = "crane_scale_results";

    //Save String======
    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_CS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCE_CS, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }
}