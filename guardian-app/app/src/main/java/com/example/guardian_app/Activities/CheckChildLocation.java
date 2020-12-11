package com.example.guardian_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guardian_app.Domain.CipherHandling;
import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.PrivateKey;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckChildLocation extends AppCompatActivity {
    private DataStore dataStore;
    private String childToLocate;
    private String childrenIdToLocate;
    private InfoRetreiverApi infoRetreiverApi;
    private TextView textViewresult;
    private PrivateKey privateKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_child_location);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            childToLocate = extras.getString("childToLocate");
            privateKey = dataStore.getPrivateKey();
        }

        childrenIdToLocate = dataStore.getAssociationByChildName(childToLocate);

        textViewresult = (TextView) findViewById(R.id.text_view_result);

        infoRetreiverApi = RetrofitCreator.retrofitApiCreator(getApplicationContext());

        getChildLocation(childrenIdToLocate);
    }

    public void getChildLocation(String childrenIdToLocate) {

        Call<JsonObject> call = infoRetreiverApi.getLocation(childrenIdToLocate);
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    textViewresult.setText("Response code" + response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();
                String jsonStr = jsonObject1.get("value").getAsString().replace("\\", "");

                JsonObject jo2 = new JsonParser().parse(jsonStr).getAsJsonObject();

                String key = jo2.get("key").getAsString();
                String data = jo2.get("data").getAsString();

                SecretKey secretKey = CipherHandling.decipherKey(key, privateKey);
                String location = CipherHandling.decipherData(secretKey, data);

                String content = "Last known location about your child " + childToLocate + "\n"+ location;

                textViewresult.setText(content);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                textViewresult.setText(t.getMessage());
            }
        });

    }

    public void updateLocation(View view) {

        Toast.makeText(getApplicationContext(),"Update Requested!", Toast.LENGTH_SHORT).show();
        getChildLocation(childrenIdToLocate);
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

}