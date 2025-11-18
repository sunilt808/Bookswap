package com.example.bookswap.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "BookSwapPrefs";
    private static final String KEY_TOKEN = "jwt_token";

    SharedPreferences pref;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        pref.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void logout() {
        pref.edit().remove(KEY_TOKEN).apply();
    }
}
