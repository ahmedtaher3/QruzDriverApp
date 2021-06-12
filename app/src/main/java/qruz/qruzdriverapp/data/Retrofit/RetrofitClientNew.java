package qruz.qruzdriverapp.data.Retrofit;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import qruz.qruzdriverapp.Helper.URLHelper;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ahmed Taher on 10/20/2018.
 */

public class RetrofitClientNew {

    private static Retrofit ourInstance;

    static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public static Retrofit getInstance() {
        if (ourInstance == null)
            ourInstance = new Retrofit.Builder()
                    .baseUrl(URLHelper.base)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();


            return ourInstance;
    }

    private RetrofitClientNew() {
    }

}
