package com.example.child_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.child_app.RetrofitAPI.CommunicationInterface;
import com.example.child_app.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class LocationActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView t;
    private Timer timer;
    private TimerTask task;
    private CommunicationInterface communicationInterface;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private String associationId;
    private String guardiansPublicKey;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            associationId = extras.getString("associationId");
            guardiansPublicKey = extras.getString("publicKey");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://144.64.187.232:9000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        communicationInterface = retrofit.create(CommunicationInterface.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        t = findViewById(R.id.locationText);
        timer = new Timer();
        task = new Task(this);
        timer.scheduleAtFixedRate(this.task, 3000, 10000);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY,null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            t.setText("");
                            @SuppressLint("DefaultLocale") String message = String.format("%s\n%f, %f", new Date(location.getTime()).toString(), location.getLatitude(), location.getLongitude());
                            t.setText(message);

                            //generate Y key and cipher data
                            SecretKey secretKey = generateSecretKey();
                            String cipheredKey = cipherKey(secretKey, guardiansPublicKey);
                            String cipheredData =  cipherData(secretKey ,message);
                            JsonObject json = new JsonObject();
                            json.addProperty("associationId", associationId);

                            JsonObject payload = new JsonObject();
                            payload.addProperty("key", cipheredKey);
                            payload.addProperty("data", cipheredData);

                            json.addProperty("data", String.valueOf(payload));

                            Call<JsonObject> call = communicationInterface.sendInfo(json);
                            call.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if (!response.isSuccessful()){
                                        t.setText("Code: " + response.code());
                                    }
                                    t.setText(response.toString());
                                    JsonObject jsonObject1 = response.body();

                                    String content = "";
                                    content += jsonObject1.get("ack");
                                    t.setText(content);
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable error) {
                                    t.setText(error.getMessage());
                                }
                            });
                        }
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private SecretKey generateSecretKey() {
        try {
            System.out.println("Generating symmetric keys for AES algorithm");
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128); // The AES key size in number of bits
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
         // for example
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String cipherKey(SecretKey secretKey, String guardiansPublicKey) {
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(guardiansPublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);

            String secretKeyStr = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            return Base64.getEncoder().encodeToString(encrypt(secretKeyStr, publicKey));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] encrypt(String data, PublicKey publicKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        System.out.println("Ciphering AES key with RSA/ECB/PKCS1Padding algorithm...");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String cipherData(SecretKey secretKey, String data) {
        try {

            final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
            System.out.println("Ciphering data with " + CIPHER_ALGO + "...");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}