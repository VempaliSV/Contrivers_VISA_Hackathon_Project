package com.example.merchantapp.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;
import java.sql.Timestamp;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "Login_details";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String USER_SAVED = "UserSaved";
    public static final String ACCESS_TOKEN = "AccessToken";
    public static final String REFRESH_TOKEN = "RefreshToken";
    public static final String USERNAME = "Username";
    public static final String EMAIL = "Email";
    public static final String STATE = "State";
    public static final String ZIP = "Zip";
    public static final String MOBILE_NUMBER = "MobileNumber";
    public static final String COUNTRY = "Country";
    // Login TimeStamp
    public static final String LOGIN_TIME = "LoginTime";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Creating login session
    public void createLoginSession(String access_token, String refresh_token){
        // Storing access token in pref
        editor.putString(ACCESS_TOKEN, access_token);
        // Storing refresh token in pref
        editor.putString(REFRESH_TOKEN, refresh_token);
        // Storing Login time in pref as long.
        Timestamp current_time = new Timestamp(System.currentTimeMillis());
        editor.putLong(LOGIN_TIME,current_time.getTime());
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        // commit changes
        editor.commit();
    }

    // Creating user session
    public void createUserSession(String username, String email, String mobile_number, String country, String state, String zip_code){
        editor.putString(USERNAME, username);
        editor.putString(MOBILE_NUMBER, mobile_number);
        editor.putString(COUNTRY, country);
        editor.putBoolean(USER_SAVED, true);
        editor.putString(EMAIL, email);
        editor.putString(STATE, state);
        editor.putString(ZIP, zip_code);
        editor.commit();
    }

    // Getting stored session data
    public String getAccessToken(){
        return pref.getString(ACCESS_TOKEN,null);
    }
    public String getRefreshToken(){
        return pref.getString(REFRESH_TOKEN,null);
    }
    public String getUsername(){
        return pref.getString(USERNAME,null);
    }
    public String getEmail(){
        return pref.getString(EMAIL,null);
    }
    public String getMobileNumber(){
        return pref.getString(MOBILE_NUMBER,null);
    }
    public String getCountry(){
        return pref.getString(COUNTRY,null);
    }

    // Clearing session details
    public void clearTokens(){
        editor.clear();
        editor.commit();
    }

    // Quick check for login
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN,false);
    }

    public boolean isTimeOver(){
        Timestamp timeNow = new Timestamp(System.currentTimeMillis());
        return ((timeNow.getTime() - pref.getLong(LOGIN_TIME,0)) > 3600000); // 1 hr
    }
}