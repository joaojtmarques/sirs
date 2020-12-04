package com.example.guardian_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.R;

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
        Intent intent = new Intent(this, SelectChildToLocate.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

}