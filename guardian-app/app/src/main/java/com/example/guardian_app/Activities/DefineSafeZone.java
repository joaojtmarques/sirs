package com.example.guardian_app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Dialogs.ZoneDefinedDialog;
import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.R;

import java.util.ArrayList;

public class DefineSafeZone extends AppCompatActivity implements ZoneDefinedDialog.DialogListener {
    private DataStore dataStore;
    private Button button;

    private EditText latitudeText;
    private EditText longitudeText;
    private EditText rangeText;

    private float latitude;
    private float longitude;
    private float range;
    private String childChosen;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.define_safe_zone);

        Log.i("onCreate", "on Create!");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            childChosen = extras.getString("childChosen");
        }
        latitudeText = (EditText) findViewById(R.id.latitude);
        longitudeText = (EditText) findViewById(R.id.longitude);
        rangeText = (EditText) findViewById(R.id.range);

        button = (Button) findViewById(R.id.submitButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    saveSafeZone();
                    openDialog();
                }
            }
        });

    }

    private void openDialog() {
        ZoneDefinedDialog dialog = new ZoneDefinedDialog();

        dialog.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void okButtonPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    public boolean validateInput() {
        String latitudeInput = latitudeText.getText().toString();
        String longitudeInput = longitudeText.getText().toString();
        String rangeInput = rangeText.getText().toString();
        return (latitudeInputCheck(latitudeInput) && longitudeInputCheck(longitudeInput) && rangeInputCheck(rangeInput));
    }

    public boolean longitudeInputCheck(String longitudeInput) {
        if (longitudeInput.isEmpty()) {
            longitudeText.setError("Field cannot be empty!");
            return false;
        }
        else {
            try {
                longitude = Float.parseFloat(longitudeInput.replace(",", "."));
            }
            catch (NumberFormatException e) {
                longitudeText.setError("Value must between -180 and 180!");
            }
            if (longitude > 180 || longitude < -180) {
                longitudeText.setError("Value must be between -180 and 180!");
                return false;
            }
        }
        return true;
    }

    public boolean rangeInputCheck(String rangeInput) {
        if (rangeInput.isEmpty()) {
            rangeText.setError("Field cannot be empty!");
            return false;
        }
        else {
            try {
                range = Float.parseFloat(rangeInput);
            }
            catch (NumberFormatException e) {
                rangeText.setError("Value must be an Integer");
            }
            if (range < 0) {
                rangeText.setError("Value must be greater than 0");
                return false;
            }
        }
        return true;
    }

    public boolean latitudeInputCheck(String latitudeInput) {
        if (latitudeInput.isEmpty()) {
            latitudeText.setError("Field cannot be empty!");
            return false;
        }
        else {
            try {
                latitude = Float.parseFloat(latitudeInput.replace(",", "."));
            }
            catch (NumberFormatException e) {
                latitudeText.setError("Value must be between -90 and 90!");
            }
            if (latitude > 90 || latitude < -90) {
                latitudeText.setError("Value must be between -90 and 90!");
                return false;
            }
        }
        return true;
    }

    private void saveSafeZone() {
        ArrayList<Float> safeZone = new ArrayList<Float>();
        safeZone.add(latitude);
        safeZone.add(longitude);
        safeZone.add(range);
        dataStore.addSafeZone(childChosen, safeZone);
    }

}
