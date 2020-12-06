package com.example.guardian_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.R;

import java.util.Base64;

public class MainActivity extends AppCompatActivity {
    private DataStore dataStore;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");

        }
        else {
            dataStore = new DataStore();
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToAddChild (View view){
        Intent intent = new Intent(this, AddChildActivity.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("publicKey", dataStore.getPublicKeyAsString());
        startActivity(intent);
    }

    public void goToCheckMonitoringChildren (View view){
        Intent intent = new Intent(this, CheckMonitoringChildren.class);
        startActivity(intent);
    }

    public void goToCheckChildLocation(View view){
        Intent intent = new Intent(this, SelectChildToLocate.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("privateKey", dataStore.getPrivateKey());
        startActivity(intent);
    }

}