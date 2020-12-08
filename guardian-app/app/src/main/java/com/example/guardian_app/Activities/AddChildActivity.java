package com.example.guardian_app.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;


import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChildActivity extends AppCompatActivity {
    private InfoRetreiverApi infoRetreiverApi;

    private String childName;
    private Button submitButton;
    private EditText nameInput;
    private DataStore dataStore;
    private String childCode;
    private String publicKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_child_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            publicKey = extras.getString("publicKey");
        }


        nameInput = (EditText) findViewById(R.id.childsName);
        submitButton = (Button) findViewById(R.id.submitButton);

        infoRetreiverApi = RetrofitCreator.retrofitApiCreator();

        try {
            createBindRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void goToGenerateQrCode (View view){
        childName = nameInput.getText().toString();
        if (childName.isEmpty()) {
            nameInput.setError("Field cannot be empty!");
        }
        else {
            Intent intent = new Intent(this, GenerateQrCode.class);
            dataStore.addAssociation(childName, childCode);
            intent.putExtra("dataStore", dataStore);
            intent.putExtra("childCode", childCode);
            intent.putExtra("publicKey", publicKey);
            startActivity(intent);
        }
    }


    private void createBindRequest() throws JSONException {
        JsonObject bindRequest = new JsonObject();
        bindRequest.addProperty("publicKey", publicKey);
        Call<JsonObject> call = infoRetreiverApi.createBindRequest(bindRequest);
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
                content += jsonObject1.get("id");
                setChildCode(jsonObject1.get("id").getAsString());
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
