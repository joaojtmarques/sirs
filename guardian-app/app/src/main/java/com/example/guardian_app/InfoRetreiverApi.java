package com.example.guardian_app;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface InfoRetreiverApi {

    @GET("infos")
    Call<List<Info>> getInfo();

    @POST("bind-request")
    Call<JsonObject> createBindRequest(@Body JsonObject bindRequest);

    @POST("bind-check")
    Call<JsonObject> wasBindSuccessful(@Body JsonObject bindCode);
}
