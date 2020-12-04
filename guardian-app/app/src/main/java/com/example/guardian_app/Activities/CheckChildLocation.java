package com.example.guardian_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckChildLocation extends AppCompatActivity {
    private DataStore dataStore;
    private String childToLocate;
    private String childrenIdToLocate;
    private InfoRetreiverApi infoRetreiverApi;
    private TextView textViewresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_child_location);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            childToLocate = extras.getString("childToLocate");
        }

        childrenIdToLocate = dataStore.getAssociationByChildName(childToLocate);

        textViewresult = (TextView) findViewById(R.id.text_view_result);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://144.64.187.232:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);

        getChildLocation(childrenIdToLocate);
    }

    private void getChildLocation(String childrenIdToLocate) {

        Call<JsonObject> call = infoRetreiverApi.getLocation(childrenIdToLocate);
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    textViewresult.setText("Response code" + response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();
                JsonArray ja = jsonObject1.getAsJsonArray("locations");
                System.out.println(ja);
                JsonObject jo = ja.get(0).getAsJsonObject();
                String yau = jo.get("value").toString();
                System.out.println(yau);
                String content = "Your child is located at: " + yau;
                textViewresult.setText("OLA!");
                System.out.println(content);
                textViewresult.setText(content);


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                textViewresult.setText(t.getMessage());
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