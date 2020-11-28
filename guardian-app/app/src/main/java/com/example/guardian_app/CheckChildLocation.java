package com.example.guardian_app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckChildLocation extends AppCompatActivity {
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_child_location);
        System.out.println("here");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewResult = findViewById(R.id.text_view_result);


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        InfoRetreiverApi infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);

        Call<List<Info>> call = infoRetreiverApi.getInfo();

        call.enqueue(new Callback<List<Info>>() {
            @Override
            public void onResponse(Call<List<Info>> call, Response<List<Info>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("Code: " + response.code());
                    return;
                }

                List<Info> infos = response.body();
                for (Info info : infos) {
                    String content = "";
                    content += "ID: " + info.getId() + "\n";
                    content += "Location " + info.getLocation() + "\n";
                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<Info>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }
}
