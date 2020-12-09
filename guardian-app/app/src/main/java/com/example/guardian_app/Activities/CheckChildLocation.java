package com.example.guardian_app.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckChildLocation extends AppCompatActivity {
    private DataStore dataStore;
    private String childToLocate;
    private String childrenIdToLocate;
    private InfoRetreiverApi infoRetreiverApi;
    private TextView textViewresult;
    private PrivateKey privateKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_child_location);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            childToLocate = extras.getString("childToLocate");
            privateKey = dataStore.getPrivateKey();
        }

        childrenIdToLocate = dataStore.getAssociationByChildName(childToLocate);

        textViewresult = (TextView) findViewById(R.id.text_view_result);

        infoRetreiverApi = RetrofitCreator.retrofitApiCreator(getApplicationContext());

        getChildLocation(childrenIdToLocate);
    }

    public void getChildLocation(String childrenIdToLocate) {

        Call<JsonObject> call = infoRetreiverApi.getLocation(childrenIdToLocate);
        call.enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    textViewresult.setText("Response code" + response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();
                String jsonStr = jsonObject1.get("value").getAsString().replace("\\", "");

                JsonObject jo2 = new JsonParser().parse(jsonStr).getAsJsonObject();

                String key = jo2.get("key").getAsString();
                String data = jo2.get("data").getAsString();
                System.out.println(key);
                System.out.println(data);

                SecretKey secretKey = decipherKey(key);
                String location = decipherData(secretKey, data);

                String content = "Last known location about your child: " + location;

                textViewresult.setText(content);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                textViewresult.setText(t.getMessage());
            }
        });

    }

    public void updateLocation(View view) {
        getChildLocation(childrenIdToLocate);
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("dataStore", dataStore);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private SecretKey decipherKey(String key) {
        try {

            String decryptedKey = decrypt(Base64.getDecoder().decode(key.getBytes()), privateKey);

            byte[] keyAsByteArray = Base64.getDecoder().decode(decryptedKey.getBytes());

            return new SecretKeySpec(keyAsByteArray, 0, keyAsByteArray.length, "AES");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println("Deciphering AES key with RSA/ECB/PKCS1Padding Algorithm...");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String decipherData(SecretKey secretKey, String message) {
        try {
            final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
            System.out.println("Deciphering data with " + CIPHER_ALGO + "...");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(message)));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getChildrenIdToLocate() {
        return childrenIdToLocate;
    }
}