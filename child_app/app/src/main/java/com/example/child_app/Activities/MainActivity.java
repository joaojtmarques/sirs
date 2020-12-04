package com.example.child_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.example.child_app.Activities.LocationActivity;
import com.example.child_app.Activities.ReadQrCode;
import com.example.child_app.R;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "on Create!!!");

        setContentView(R.layout.activity_main);



    }
    public void goToPairWithTutor (View view){
        Intent intent = new Intent(this, ReadQrCode.class);
        startActivity(intent);

    }

    public void goToLocationActivity (View view){
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }
    /*
    public void goToCheckChildLocation(View view){
        Intent intent = new Intent(this, CheckChildLocation.class);
        startActivity(intent);
    }
    */
}