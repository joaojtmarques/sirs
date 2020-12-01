package com.example.guardian_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChildAdded extends AppCompatActivity{
    private TextView textViewResult;
    private InfoRetreiverApi infoRetreiverApi;
    private DataStore dataStore;
    private String childName;
    private String childCode;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_added_activity);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            System.out.println(dataStore.getChildNames());
        }
        textViewResult = (TextView)findViewById(R.id.text_view_result);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);

        //wasBindSuccessful();

        textViewResult.setText("SUCCESS!");

    }

    private void wasBindSuccessful() {
        Call<Boolean> call = infoRetreiverApi.wasBindSuccessful("code");
        call.enqueue(new Callback<Boolean>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                textViewResult.setText("code:" + response.code());
                return;
            }
            /*
            if (response.body() == true) {
                textViewResult.setText("Child was successfully added to the system!");
            }
            else {
                textViewResult.setText("Something went wrong");
            }
            */
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }


    public void goToMainActivity (View view){
        Intent intent = new Intent(this, MainActivity.class);
        System.out.println(dataStore.getChildNames());
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }


}

