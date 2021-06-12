package qruz.qruzdriverapp.base;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import qruz.qruzdriverapp.Utilities.FontsOverride;
import qruz.qruzdriverapp.Utilities.LocaleUtils;
import qruz.qruzdriverapp.data.local.DataManager;
import qruz.qruzdriverapp.data.local.SharedPrefsHelper;

public class BaseApplication extends android.app.Application {
    DataManager dataManager;
    public static final String TAG = BaseApplication.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static BaseApplication mInstance;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(this, newConfig);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(this);
        dataManager = new DataManager(sharedPrefsHelper);
        mInstance = this;
//        initCalligraphyConfig();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "ClanPro-NarrBook.otf");
//        FontsOverride.setDefaultFont(this, "DEFAULT", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "SERIF", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "ClanPro-Book.otf");
    }

    private void initCalligraphyConfig() {

    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json) {
        String trimmedString = "";

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form", "" + value.getString(i));
                        trimmedString += value.getString(i);
                        if (i < size - 1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed", "" + trimmedString);

        return trimmedString;
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}

