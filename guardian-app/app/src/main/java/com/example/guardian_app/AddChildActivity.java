package com.example.guardian_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;


import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddChildActivity extends AppCompatActivity {
    private InfoRetreiverApi infoRetreiverApi;
    private TextView textViewResult2;

    private String childName;
    private Button submitButton;
    private EditText nameInput;
    private DataStore dataStore;

    private String childCode;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_child_activity);
        System.out.println("here");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
        }


        //textViewResult2 = (TextView)findViewById(R.id.text_view_result2);

        nameInput = (EditText) findViewById(R.id.childsName);
        submitButton = (Button) findViewById(R.id.submitButton);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);
        createBindRequest();


    }

    public void goToGenerateQrCode (View view){
        childName = nameInput.getText().toString();
        Intent intent = new Intent(this, GenerateQrCode.class);
        dataStore.addAssociation(childName, childCode);
        System.out.println(dataStore.getChildNames());
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }



    private void createBindRequest() {
        Call<JsonObject> call = infoRetreiverApi.createBindRequest("mamasMamas");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                    return;
                }
                //JSONObject responseObj = null;
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAaa\n" + response.body() + "AAAAAAAAAAAAAAAAAAAAAAAAAA\n");
                JsonObject jsonObject1 = response.body();
                //JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();


                //String responseString = response.body();

                String content = "";
                content += "Code: " + response.code();
                content += jsonObject1.get("id");
                setChildCode(jsonObject1.get("id").getAsString());
                //childCode = jsonObject1.get("id").getAsString();
                System.out.println("Child Cide from JSONOBJECT" + childCode);

                System.out.println(content);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    public void setChildCode(String childCode) {
        this.childCode = childCode;
    }
}
