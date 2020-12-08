package com.example.guardian_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.guardian_app.Dialogs.ZoneAlreadyDefinedDialog;
import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.Domain.CipherHandling;
import com.example.guardian_app.R;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ZoneAlreadyDefinedDialog.DefinedDialogListener{
    private DataStore dataStore;
    private Timer timer;
    private TimerTask task;
    private CipherHandling locationRequestHandler;
    private InfoRetreiverApi infoRetreiverApi;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            System.out.println("Latitude: " + dataStore.getLatitude());
            System.out.println("Longitude: " + dataStore.getLongitude());
            System.out.println("Range: " + dataStore.getRange());

        }
        else {
            dataStore = new DataStore();
        }
        if (dataStore.getChildNames().size() > 0 && dataStore.hasSafeZoneDefined()) {
            handleLocationRequestsOnBackground();
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void goToAddChild (View view){
        Intent intent = new Intent(this, AddChildActivity.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("publicKey", dataStore.getPublicKeyAsString());
        startActivity(intent);
    }

    public void goToDefineSafeZone(View view){
        if (dataStore.hasSafeZoneDefined()) {
            openDialog();
        }
        else {
            Intent intent = new Intent(this, DefineSafeZone.class);
            intent.putExtra("dataStore", dataStore);
            startActivity(intent);
        }

    }

    public void goToCheckChildLocation(View view){
        Intent intent = new Intent(this, SelectChildToLocate.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("privateKey", dataStore.getPrivateKey());
        startActivity(intent);
    }

    @Override
    public void okButtonPressed() {
        dataStore.deleteSafeZone();
        task.cancel();
        timer.cancel();
        Intent intent = new Intent(this, DefineSafeZone.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    @Override
    public void cancelButtonPressed() {

    }

    public void openDialog() {
        ZoneAlreadyDefinedDialog dialog = new ZoneAlreadyDefinedDialog();
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    private void handleLocationRequestsOnBackground() {
        timer = new Timer();
        task = new Task(this);
        timer.scheduleAtFixedRate(this.task, 3000, 10000);

    }



    //------------------------------------------------------------

    public void getEveryChildLocation() {
        infoRetreiverApi = RetrofitCreator.retrofitApiCreator();
        for (String child: (dataStore.getChildNames())) {
            getChildLocation(dataStore.getAssociationByChildName(child));
        }
    }

    public void getChildLocation(String childrenIdToLocate) {

        Call<JsonObject> call = infoRetreiverApi.getLocation(childrenIdToLocate);
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
                if (dataStore.hasSafeZoneDefined()) {
                    checkIfLocationIsSafe(location);
                }

                System.out.println(content);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    private void checkIfLocationIsSafe(String message) {
        String[] arrOfStr = message.split("\n");
        String[] location = arrOfStr[1].split(" ");
        float latitude = Float.parseFloat(location[0].substring(0, location[0].length()-1).replace(",", "."));
        float longitude = Float.parseFloat(location[1].replace(",", "."));
        System.out.println("Latitude: " + latitude + "and Longitude: " + longitude);
        double distanceFromCenter = calculateDistanceInMeters(dataStore.getLatitude(), dataStore.getLongitude(), latitude, longitude);
        System.out.println("Distance in meters between locations: " + distanceFromCenter);
        if (distanceFromCenter > dataStore.getRange()) {
            double distance = distanceFromCenter - dataStore.getRange();
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