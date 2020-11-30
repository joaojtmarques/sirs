package com.example.child_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView t;
    private Timer timer;
    private TimerTask task;
    private CommunicationInterface communicationInterface;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:9000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        communicationInterface = retrofit.create(CommunicationInterface.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        t = findViewById(R.id.locationText);
        timer = new Timer();
        task = new Task(this);
        timer.scheduleAtFixedRate(this.task, 3000, 10000);
    }

    protected void getCurrentLocation() {

        Log.i(null,"Request Sent");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY,null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            t.setText("");
                            @SuppressLint("DefaultLocale") String message = String.format("%s\n%f, %f", new Date(location.getTime()).toString(), location.getLatitude(), location.getLongitude());
                            t.setText(message);

                            JSONObject json = new JSONObject();
                            try {
                                json.put("id", "SOU O TEU FILHO BOY");
                                json.put("Location",location);
                                json.put("Timestamp",new Date(location.getTime()).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Call<String> call = communicationInterface.sendInfo(json);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (!response.isSuccessful()){
                                        t.setText("Code: " + response.code());
                                    }
                                    t.setText(response.toString());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable error) {
                                    t.setText(error.getMessage());
                                }
                            });
                        } else {
                            getLastLocation();
                        }
                    }
                });
    }

    private void getLastLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            t.setText("");
                            @SuppressLint("DefaultLocale") String message = String.format("%s\n%f, %f", new Date(location.getTime()).toString(), location.getLatitude(), location.getLongitude());
                            t.setText(message);

                            JSONObject json = new JSONObject();
                            try {
                                json.put("id", "SOU O TEU FILHO BOY");
                                json.put("Location",location);
                                json.put("Timestamp",new Date(location.getTime()).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Call<String> call = communicationInterface.sendInfo(json);
                            call.enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    if (!response.isSuccessful()){
                                        t.setText("Code: " + response.code());
                                    }
                                    t.setText(response.toString());
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable error) {
                                    t.setText(error.getMessage());
                                }
                            });
                        } else {
                            t.setText("Unable to get a reliable location");
                        }
                    }
                });
    }
}