package com.lefu.hetai_bleapi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;

import java.util.HashMap;

/**
 * Created by Ravi on 08/07/15.
 */
public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "ayubolifescaledevice";


    private static final String KEY_HEIGHT = "user_height";
    private static final String KEY_AGE = "user_age";
    private static final String KEY_SEX = "user_sex";
    private static final String KEY_MOBLIE = "user_mobile";
    private static final String KEY_ENTEREDED_GOAL_NAME = "entered_goal_name";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setEnteredGoalName(String data) {
        editor.putString(KEY_ENTEREDED_GOAL_NAME, data);
        editor.commit();
    }
    public String getEnteredGoalName() {
        return pref.getString(KEY_ENTEREDED_GOAL_NAME, "");
    }

    public void createLoginUser(String user_height, String user_age, String user_sex, String user_mobile) {
        editor.putString(KEY_HEIGHT, user_height);
        editor.putString(KEY_AGE, user_age);
        editor.putString(KEY_SEX, user_sex);
        editor.putString(KEY_MOBLIE, user_mobile);
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("user_height", pref.getString(KEY_HEIGHT, "0"));
        profile.put("user_age", pref.getString(KEY_AGE, "0"));
        profile.put("user_sex", pref.getString(KEY_SEX, "0"));
        profile.put("user_mobile", pref.getString(KEY_MOBLIE, "0"));
        return profile;
    }
}
