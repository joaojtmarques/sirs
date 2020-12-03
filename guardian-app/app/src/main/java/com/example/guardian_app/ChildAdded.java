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
            childCode = extras.getString("childCode");
            System.out.println(dataStore.getChildNames());
        }
        textViewResult = (TextView)findViewById(R.id.text_view_result);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://144.64.187.232:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);

        wasBindSuccessful();

        //textViewResult.setText("SUCCESS!");

    }

    private void wasBindSuccessful() {
        JsonObject wasBindSuccessful = new JsonObject();
        wasBindSuccessful.addProperty("associationId", childCode);

        Call<JsonObject> call = infoRetreiverApi.wasBindSuccessful(wasBindSuccessful);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                    return;
                }
                JsonObject jsonObject1 = response.body();

                textViewResult.setText(jsonObject1.get("ack").getAsString());

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
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

