package com.example.nistarak;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://mysterious-bastion-14340.herokuapp.com/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    public static String token = null;
    public static int errcode = -1, success = 1;
    public static String level;

    private RetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if( mInstance == null) {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public void reset() {
        errcode = 0;
        success = 0;
        token = null;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }
}
