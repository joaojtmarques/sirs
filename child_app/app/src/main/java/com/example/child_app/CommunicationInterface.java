package com.example.child_app;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommunicationInterface {

    @POST("post-location")
    Call<JsonObject> sendInfo(@Body JsonObject info);

    @POST("bind-confirmation")
    Call<JsonObject> createBindRequest(@Body JsonObject bindRequest);
}
