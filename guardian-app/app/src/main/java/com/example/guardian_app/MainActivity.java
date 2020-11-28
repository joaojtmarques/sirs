package com.example.guardian_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("here");

    }
    public void goToAddChild (View view){
        Intent intent = new Intent(this, AddChildActivity.class);
        startActivity(intent);
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