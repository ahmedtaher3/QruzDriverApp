package qruz.t.qruzdriverapp.data.Retrofit;


import java.util.Map;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import qruz.t.qruzdriverapp.Helper.URLHelper;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by CSS on 8/4/2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/provider/trip/{id}/calculate")
    Call<ResponseBody> getLiveTracking(@Header("X-Requested-With") String xmlRequest, @Header("Authorization") String strToken,
                                       @Path("id") String id,
                                       @Field("latitude") String latitude, @Field("longitude") String longitude);


    @POST(URLHelper.register)
    @FormUrlEncoded
    Single<ResponseBody> register_user(@FieldMap Map<String, String> qStringMap);
}
