package com.example.guardian_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.Domain.CipherHandling;
import com.example.guardian_app.R;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private DataStore dataStore;
    private Timer timer;
    private TimerTask task;
    private InfoRetreiverApi infoRetreiverApi;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate" , "On Create!");
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            System.out.println("Is safe Zone empty: " + dataStore.isSafeZoneMapEmpty());
        }
        else {
            dataStore = new DataStore();
        }
        if (dataStore.getChildNames().size() > 0 && !dataStore.isSafeZoneMapEmpty()) {
            handleLocationRequestsOnBackground();
        }


    }

    @Override
    protected void onResume() {
        Log.i("onResume" , "On Resume!");
        super.onResume();
        System.out.println(dataStore.getChildNames());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToAddChild (View view){
        if (task != null && timer != null) {
            task.cancel();
            timer.cancel();
        }

        Intent intent = new Intent(this, AddChildActivity.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("publicKey", dataStore.getPublicKeyAsString());
        startActivity(intent);
    }

    public void goToSelectChildForSafeZone(View view){
        if (task != null && timer != null) {
            task.cancel();
            timer.cancel();
        }
        Intent intent = new Intent(this, SelectChildForSafeZone.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    public void goToCheckChildLocation(View view){
        if (task != null && timer != null) {
            task.cancel();
            timer.cancel();
        }
        Intent intent = new Intent(this, SelectChildToLocate.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("privateKey", dataStore.getPrivateKey());
        startActivity(intent);
    }



    private void handleLocationRequestsOnBackground() {
        timer = new Timer();
        task = new Task(this);
        timer.scheduleAtFixedRate(this.task, 3000, 10000);

    }



    //------------------------------------------------------------

    public void getEveryChildLocation() {
        infoRetreiverApi = RetrofitCreator.retrofitApiCreator(getApplicationContext());
        for (String child: (dataStore.getChildNames())) {
            getChildLocation(child);
        }
    }

    public void getChildLocation(String childName) {

        Call<JsonObject> call = infoRetreiverApi.getLocation(dataStore.getAssociationByChildName(childName));
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Response code" + response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();
                String jsonStr = jsonObject1.get("value").getAsString().replace("\\", "");

                JsonObject jo2 = new JsonParser().parse(jsonStr).getAsJsonObject();

                String key = jo2.get("key").getAsString();
                String data = jo2.get("data").getAsString();
                System.out.println(key);
                System.out.println(data);

                SecretKey secretKey = CipherHandling.decipherKey(key, dataStore.getPrivateKey());
                String location = CipherHandling.decipherData(secretKey, data);

                String content = "Last known location about your child: " + location;
                if (dataStore.getSafeZoneByChildName(childName) != null) {
                    checkIfLocationIsSafe(location, childName);
                }

                System.out.println(content);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private void checkIfLocationIsSafe(String message, String childName) {
        String[] arrOfStr = message.split("\n");
        String[] location = arrOfStr[1].split(" ");
        float latitude = Float.parseFloat(location[0].substring(0, location[0].length()-1).replace(",", "."));
        float longitude = Float.parseFloat(location[1].replace(",", "."));
        ArrayList<Float> safeZoneDefined = dataStore.getSafeZoneByChildName(childName);
        System.out.println("Latitude: " + latitude + "and Longitude: " + longitude);
        double distanceFromCenter = calculateDistanceInMeters(safeZoneDefined.get(0), safeZoneDefined.get(1), latitude, longitude);
        System.out.println("Distance in meters between locations: " + distanceFromCenter);
        if (distanceFromCenter > safeZoneDefined.get(2)) {
            double distance = distanceFromCenter - safeZoneDefined.get(2);
            openDialog(distance);
        }
    }

    public double calculateDistanceInMeters(double lat1, double long1, double lat2, double long2) {
        double earthRadius = 6371e3;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(long2-long1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }


    private void openDialog( double distance) { //Added argument
        AlertDialog alertDialog = new AlertDialog.Builder(this).create(); //Use context
        alertDialog.setTitle("Your child is outside of Safe Zone!");
        alertDialog.setMessage("Your child is " + distance + " meters away from Safe Zone");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }




}