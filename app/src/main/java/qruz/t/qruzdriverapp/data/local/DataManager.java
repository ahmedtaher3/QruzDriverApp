package qruz.t.qruzdriverapp.data.local;

import com.google.gson.Gson;

import qruz.t.qruzdriverapp.model.User;

/**
 * Created by A.taher on 10/15/2019.
 */

public class DataManager {


    private SharedPrefsHelper mSharedPrefsHelper;

    public DataManager(SharedPrefsHelper sharedPrefsHelper) {
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void clear() {
        mSharedPrefsHelper.clear();
    }


    public void saveUser(String user) {
        mSharedPrefsHelper.putUser(user);
    }

    public User getUser() {
        mSharedPrefsHelper.getUser();

        Gson gson = new Gson();
        String json = mSharedPrefsHelper.getUser();
        return gson.fromJson(json, User.class);
    }

    public void saveStartAtTime(Long user) {
        mSharedPrefsHelper.putStartAtTime(user);
    }

    public Long getStartAtTime() {
        return mSharedPrefsHelper.getStartAtTime();

    }


    public void saveAccessToken(String accessToken) {
        mSharedPrefsHelper.putAccessToken(accessToken);
    }

    public String getAccessToken() {
        return mSharedPrefsHelper.getAccessToken();
    }


    public void saveLoggingMode(boolean logged) {
        mSharedPrefsHelper.putLoggingMode(logged);
    }

    public boolean getLoggingMode() {
        return mSharedPrefsHelper.getLoggingMode();
    }


    public void saveIsTripLive(boolean live) {
        mSharedPrefsHelper.putIsTripLive(live);
    }

    public boolean getIsTripLive() {
        return mSharedPrefsHelper.getIsTripLive();
    }

    public void saveTripId(String s) {
        mSharedPrefsHelper.putTripId(s);
    }

    public String getTripId() {
        return mSharedPrefsHelper.getTripId();
    }

    public void saveStartAt(String s) {
        mSharedPrefsHelper.putStartAt(s);
    }

    public String getStartAt() {
        return mSharedPrefsHelper.getStartAt();
    }

    public void saveLogId(String s) {
        mSharedPrefsHelper.putLogId(s);
    }

    public String getLogId() {
        return mSharedPrefsHelper.getLogId();
    }

    public void saveTripType(String s) {
        mSharedPrefsHelper.putTripType(s);
    }

    public String getTripType() {
        return mSharedPrefsHelper.getTripType();
    }


    public void saveLang(String name) {
        mSharedPrefsHelper.putLang(name);
    }

    public String getLang() {
        return mSharedPrefsHelper.getLang();
    }
}


