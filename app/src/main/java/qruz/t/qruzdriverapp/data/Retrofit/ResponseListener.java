package qruz.t.qruzdriverapp.data.Retrofit;

import org.json.JSONArray;

/**
 * Created by Esack N on 7/24/2017.
 */

public interface ResponseListener {
    void getJSONArrayResult(String strTag, JSONArray arrayResponse);
}