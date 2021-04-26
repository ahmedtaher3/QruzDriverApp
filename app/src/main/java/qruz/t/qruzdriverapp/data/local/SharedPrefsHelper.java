/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package qruz.t.qruzdriverapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by A.taher on 10/15/2019.
 */

public class SharedPrefsHelper {

    private static final String MY_PREFS = "MY_PREFS";
    private static final String USER = "USER";
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String LOGGED_IN = "LOGGED_IN";
    private static final String IS_TRIP_LIVE = "IS_TRIP_LIVE";
    private static final String TRIP_ID = "TRIP_ID";
    private static final String StartAt = "StartAt";
    private static final String LOG_ID = "LOG_ID";
    private static final String TRIP_TYPE = "TRIP_TYPE";
    private static final String LANG = "LANG";
    private static final String START_AT = "START_AT";


    private SharedPreferences mSharedPreferences;

    public SharedPrefsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
    }

    void clear() {
        mSharedPreferences.edit().clear().apply();
    }


    void putUser(String name) {
        mSharedPreferences.edit().putString(USER, name).apply();
    }

    String getUser() {
        return mSharedPreferences.getString(USER, null);
    }


    void putAccessToken(String accessToken) {
        mSharedPreferences.edit().putString(ACCESS_TOKEN, accessToken).apply();
    }

    String getAccessToken() {
        return mSharedPreferences.getString(ACCESS_TOKEN, null);
    }


    void putLoggingMode(boolean logged) {
        mSharedPreferences.edit().putBoolean(LOGGED_IN, logged).apply();
    }

    boolean getLoggingMode() {
        return mSharedPreferences.getBoolean(LOGGED_IN, false);
    }


    void putIsTripLive(boolean b) {
        mSharedPreferences.edit().putBoolean(IS_TRIP_LIVE, b).apply();
    }

    boolean getIsTripLive() {
        return mSharedPreferences.getBoolean(IS_TRIP_LIVE, false);
    }


    void putTripId(String s) {
        mSharedPreferences.edit().putString(TRIP_ID, s).apply();
    }

    String getTripId() {
        return mSharedPreferences.getString(TRIP_ID, null);
    }

    void putStartAt(String s) {
        mSharedPreferences.edit().putString(StartAt, s).apply();
    }

    String getStartAt() {
        return mSharedPreferences.getString(StartAt, null);
    }


    void putLogId(String s) {
        mSharedPreferences.edit().putString(LOG_ID, s).apply();
    }

    String getLogId() {
        return mSharedPreferences.getString(LOG_ID, null);
    }

    void putTripType(String s) {
        mSharedPreferences.edit().putString(TRIP_TYPE, s).apply();
    }

    String getTripType() {
        return mSharedPreferences.getString(TRIP_TYPE, null);
    }


    public void putLang(String name) {
        mSharedPreferences.edit().putString(LANG, name).apply();
    }

    public String getLang() {
        return mSharedPreferences.getString(LANG, "en");

    }


    public void putStartAtTime(Long name) {
        mSharedPreferences.edit().putLong(START_AT, name).apply();
    }

    public Long getStartAtTime() {
        return mSharedPreferences.getLong(START_AT, 0);

    }
}
