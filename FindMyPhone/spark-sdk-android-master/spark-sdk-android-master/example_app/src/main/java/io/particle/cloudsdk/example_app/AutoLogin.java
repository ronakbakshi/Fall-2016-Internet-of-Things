/*
This class allows user to do auto-login to particle cloud through shared preferences
 */

package io.particle.cloudsdk.example_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class AutoLogin {

    static final String PREF_USER_NAME= "username";
    static final String PREF_PASSWORD = "password";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void setPassword(Context ctx, String password)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_PASSWORD, password);
        editor.commit();
    }

    public static String getPassword(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PASSWORD, "");
    }
}
