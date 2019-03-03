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


//public interface ApiInterface {
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/auth/login")
//    Call<ResponseBody> login(
//            @Field("email") String email,
//            @Field("password") String password
//    );
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/auth/register")
//    Call<ResponseBody> register(
//            @Field("name") String name,
//            @Field("email") String email,
//            @Field("password") String password,
//            @Field("adhaar") String uniqueID,
//            @Field("accessCode") Integer level
//    );
//
//    @FormUrlEncoded
//    @POST("https://mysterious-bastion-14340.herokuapp.com/notification/get")
//    Call<ResponseBody> notification(
//            @Field("district") String district
//    );
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/disease/get")
//    Call<ResponseBody> disease(
//            @Field("district") String district
//    );
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/auth/logout")
//    Call<ResponseBody> logout(
//            @Field("token") String token
//    );
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/fetch/get_district_stats")
//    Call<ResponseBody> get_district_stats(
//            @Field("dummy") String dummy
//    );
//
//    @FormUrlEncoded
//    @POST("https://anant2k19.herokuapp.com/patient")
//    Call<ResponseBody> newDiseaseCase(
//            @Field("token") String token,
//            @Field("name") String patientName,
//            @Field("diseaseName") String diseaseName,
//            @Field("lat") Double lat,
//            @Field("long") Double lang,
//            @Field("age") Integer age,
//            @Field("adhaaar") String adhaar,
//            @Field("city") String city
//    );
//
//    @Multipart
//    @POST("https://anant2k19.herokuapp.com/bulk_add_case")
//    Call<ResponseBody> bulk_add(
//            @Part("description")RequestBody description,
//            @Part MultipartBody.Part file
//    );
//
//
//}

