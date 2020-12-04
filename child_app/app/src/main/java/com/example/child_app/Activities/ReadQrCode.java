package com.example.child_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.child_app.RetrofitAPI.CommunicationInterface;
import com.example.child_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ReadQrCode extends AppCompatActivity {

    public static TextView resultTextView;
    private Button scan_btn;
    private CommunicationInterface communicationInterface;
    private String associationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "on Create!!!");

        setContentView(R.layout.read_qrcode_activity);

        resultTextView = (TextView) findViewById(R.id.result_text);
        scan_btn = (Button) findViewById(R.id.btn_scan_qrCode);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });

        System.out.println("Result = " + resultTextView.getText().toString());

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Result = " + resultTextView.getText().toString());
        if (!resultTextView.getText().toString().equals("Result")) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://144.64.187.232:9000/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            communicationInterface = retrofit.create(CommunicationInterface.class);
            createBindRequest(resultTextView.getText().toString());
        }



    }

    private void createBindRequest(String json) {
        JsonObject bindRequest = new JsonParser().parse(json).getAsJsonObject();
        associationId = bindRequest.get("associationId").getAsString();
        System.out.println(associationId);
        Call<JsonObject> call = communicationInterface.createBindRequest(bindRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();

                String content = "";
                content += "Code: " + response.code();
                content += jsonObject1.get("ack");
                System.out.println("Bind Result" + content);

                if (jsonObject1.get("ack").getAsString().equals("Bind successful.")) {
                    goToLocationActivity();
                }
                else {
                    System.out.println("something went wrong");
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void goToLocationActivity () {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("associationId", associationId);
        startActivity(intent);
    }

}