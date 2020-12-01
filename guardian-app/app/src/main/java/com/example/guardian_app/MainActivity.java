package com.example.guardian_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "on Create!!!");

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");

        }
        else {
            dataStore = new DataStore();
            System.out.println(extras);
        }

        System.out.println("here");

        System.out.println(dataStore.getChildNames());


    }
    public void goToAddChild (View view){
        Intent intent = new Intent(this, AddChildActivity.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
        //finish();
    }

    public void goToCheckMonitoringChildren (View view){
        Intent intent = new Intent(this, CheckMonitoringChildren.class);
        startActivity(intent);
    }

    public void goToCheckChildLocation(View view){
        Intent intent = new Intent(this, CheckChildLocation.class);
        startActivity(intent);
    }

}