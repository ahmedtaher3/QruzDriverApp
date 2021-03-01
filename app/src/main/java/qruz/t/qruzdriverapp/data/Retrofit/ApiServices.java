package qruz.t.qruzdriverapp.data.Retrofit;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import qruz.t.qruzdriverapp.model.DriverRetrofit;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface ApiServices {
    public static final String BaseURL = "https://qruz.xyz/api/";

    @Multipart
    @POST("driver/avatar/update")
    @Headers({"Accept: application/json", "X-Requested-With: XMLHttpRequest"})
    Observable<DriverRetrofit> update_image(@Header("Authorization") String str, @Part("id") RequestBody requestBody, @Part MultipartBody.Part part);

    @GET
    Call<ResponseBody> getUsers(@Url String url);


}
