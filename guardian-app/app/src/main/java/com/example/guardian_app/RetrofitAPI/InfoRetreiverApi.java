package com.example.guardian_app.RetrofitAPI;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface InfoRetreiverApi {

    @GET("get-location")
    Call<JsonObject> getLocation(@Query("id") String associationId);

    @POST("bind-request")
    Call<JsonObject> createBindRequest(@Body JsonObject bindRequest);

    @POST("bind-check")
    Call<JsonObject> wasBindSuccessful(@Body JsonObject bindCode);
}
