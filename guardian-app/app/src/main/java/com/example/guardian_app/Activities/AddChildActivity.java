package com.example.guardian_app.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import org.json.JSONException;

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
                .baseUrl("http://144.64.187.232:9000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        infoRetreiverApi = retrofit.create(InfoRetreiverApi.class);
        try {
            createBindRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void goToGenerateQrCode (View view){
        childName = nameInput.getText().toString();
        Intent intent = new Intent(this, GenerateQrCode.class);
        dataStore.addAssociation(childName, childCode);
        System.out.println(dataStore.getChildNames());
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("childCode", childCode);
        startActivity(intent);
    }



    private void createBindRequest() throws JSONException {
        String messageToSend = "publicKeyhuererere";
        JsonObject bindRequest = new JsonObject();
        bindRequest.addProperty("publicKey", messageToSend);
        System.out.println(bindRequest.toString());
        Call<JsonObject> call = infoRetreiverApi.createBindRequest(bindRequest);
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
