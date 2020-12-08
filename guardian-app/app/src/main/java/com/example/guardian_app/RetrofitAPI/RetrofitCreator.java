package com.example.guardian_app.RetrofitAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCreator {
    
    private static String baseUrl = "http://144.64.187.232:9000/";
    
    public static InfoRetreiverApi retrofitApiCreator() {
        
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(InfoRetreiverApi.class);
    }

}
