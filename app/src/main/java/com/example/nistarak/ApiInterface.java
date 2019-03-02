package com.example.nistarak;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
            @Field("adhaar") String uniqueID,
            @Field("accessCode") Integer level
    );

    @FormUrlEncoded
    @POST("auth/alerts")
    Call<ResponseBody> alert(
            @Field("district") String district
    );

    @FormUrlEncoded
    @POST("auth/logout")
    Call<ResponseBody> logout(
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("/fetch/get_district_stats")
    Call<ResponseBody> get_district_stats(
            @Field("dummy") String dummy
    );

    @FormUrlEncoded
    @POST("/patient")
    Call<ResponseBody> newDiseaseCase(
            @Field("token") String token,
            @Field("name") String patientName,
            @Field("diseaseName") String diseaseName,
            @Field("lat") Double lat,
            @Field("long") Double lang,
            @Field("age") Integer age,
            @Field("adhaaar") String adhaar,
            @Field("city") String city
    );

    @Multipart
    @POST("bulk_add_case")
    Call<ResponseBody> bulk_add(
            @Part("description")RequestBody description,
            @Part MultipartBody.Part file
    );


}
