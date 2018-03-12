package com.unican.gist.gistus.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.unican.gist.gistus.R;


/**
 * Created by Andres on 09/03/2018.
 */

public class Utils {
    public Utils(){}

    public static void saveUserInPreference(Context context, String username) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.PREFERENCES_USER), encrypt(username));
        long time = System.currentTimeMillis();
        editor.commit();
    }

    public static String getUserFromPreference(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        return decrypt(sharedPref.getString(context.getString(R.string.PREFERENCES_USER), null));
    }
    public static void deleteUserPreferences(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(context.getString(R.string.PREFERENCES_USER)).commit();
    }



    public static String encrypt(String input) {
        // Simple encryption, not very strong!
        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }

}
