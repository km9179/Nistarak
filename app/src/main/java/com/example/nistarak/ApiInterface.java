package com.example.nistarak;

import java.util.ArrayList;

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
    @POST("report/apimedic")
    Call<ResponseBody> predict_disease(
            @Field("symptoms") ArrayList<Integer> symptoms,
            @Field("gender") String gender,
            @Field("dob") String dob
    );

    @FormUrlEncoded
    @POST("ngo/summary")
    Call<ResponseBody> ngo_info(
            @Field("dummy") String dummy
    );

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
    @POST("notification/get")
    Call<ResponseBody> notification(
            @Field("district") String district
    );

    @FormUrlEncoded
    @POST("/graph")
    Call<ResponseBody> graph(
        @Field("dist") String district
    );

    @FormUrlEncoded
    @POST("fetch/patientSummary")
    Call<ResponseBody> disease(
            @Field("district") String District,
            @Field("startDate") String startDate,
            @Field("endDate") String endDate
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
    @POST("/notification/create")
    Call<ResponseBody> add_notification(
            @Field("token") String token,
            @Field("notification") String notice,
            @Field("target") String target,
            @Field("district") String loc
    );


    @FormUrlEncoded
    @POST("/report/patient")
    Call<ResponseBody> newDiseaseCase(
            @Field("token") String token,
            @Field("name") String patientName,
            @Field("diseaseName") String diseaseName,
            @Field("lat") Double lat,
            @Field("lon") Double lang,
            @Field("age") Integer age,
            @Field("adhaar") String adhaar,
            @Field("city") String city
    );

    @Multipart
    @POST("bulk_add_case")
    Call<ResponseBody> bulk_add(
            @Part("description")RequestBody description,
            @Part MultipartBody.Part file
    );
}
