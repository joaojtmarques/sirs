package com.example.guardian_app.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private final static String NO_REQUESTS_AVAILABLE = "No remaining requests available.";

    private InfoRetreiverApi infoRetreiverApi;
    private String childName;
    private Button submitButton;
    private EditText nameInput;
    private DataStore dataStore;
    private String childCode;
    private String publicKey;
    private String ack;


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

        infoRetreiverApi = RetrofitCreator.retrofitApiCreator(getApplicationContext());

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
        else if (childName.length() > 30) {
            nameInput.setError("Child name cannot have more than 30 characters");
            nameInput.setText("");
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
        bindRequest.addProperty("premiumKey", DataStore.get_premiumKey());
        Call<JsonObject> call = infoRetreiverApi.createBindRequest(bindRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                    return;
                }
                JsonObject jsonObject1 = response.body();

                ack = jsonObject1.get("ack").getAsString();
                if (ack.equals(NO_REQUESTS_AVAILABLE)) {
                    Toast.makeText(getApplicationContext(),"No more children can be added", Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
                setChildCode(jsonObject1.get("id").getAsString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    public void setChildCode(String childCode) {
        this.childCode = childCode;
    }
}
