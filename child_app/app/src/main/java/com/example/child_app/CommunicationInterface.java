package com.example.child_app;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommunicationInterface {

    @POST("sendInfo")
    Call<String> sendInfo(@Body JSONObject info);
}
