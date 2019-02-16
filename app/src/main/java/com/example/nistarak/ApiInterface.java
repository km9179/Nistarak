package com.example.nistarak;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("auth/register")
    Call<ResponseBody> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("adhaar") String adhaar
//            @Field("access") Integer level
    );

    @FormUrlEncoded
    @POST("auth/logout")
    Call<ResponseBody> logout(
            @Field("token") String token
    );

}
