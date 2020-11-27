package com.example.guardian_app;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface InfoRetreiverApi {

    @GET("infos")
    Call<List<Info>> getInfo();
}
