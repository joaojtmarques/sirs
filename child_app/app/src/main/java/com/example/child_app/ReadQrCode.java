package com.example.child_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ReadQrCode extends AppCompatActivity {

    public static TextView resultTextView;
    private Button scan_btn;
    private CommunicationInterface communicationInterface;
    private String associationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "on Create!!!");

        setContentView(R.layout.read_qrcode_activity);

        resultTextView = (TextView) findViewById(R.id.result_text);
        scan_btn = (Button) findViewById(R.id.btn_scan_qrCode);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });

        System.out.println("Result = " + resultTextView.getText().toString());

        //reading qrCode from emulator path
        /*
        @SuppressLint("SdCardPath") String path ="/sdcard/Pictures/qrCode.png";
        System.out.println(path);

        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        String decoded=scanQRImage(bitmap);
        Log.i("QrTest", "Decoded string="+decoded);
        */



    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Result = " + resultTextView.getText().toString());
        if (!resultTextView.getText().toString().equals("Result")) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://144.64.187.232:9000/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            communicationInterface = retrofit.create(CommunicationInterface.class);
            createBindRequest(resultTextView.getText().toString());
        }



    }

    private void createBindRequest(String json) {
        JsonObject bindRequest = new JsonParser().parse(json).getAsJsonObject();
        associationId = bindRequest.get("associationId").getAsString();
        System.out.println(associationId);
        Call<JsonObject> call = communicationInterface.createBindRequest(bindRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()) {
                    System.out.println(response.code());
                    return;
                }

                JsonObject jsonObject1 = response.body();

                String content = "";
                content += "Code: " + response.code();
                content += jsonObject1.get("ack");
                System.out.println("Bind Result" + content);

                if (jsonObject1.get("ack").getAsString().equals("Bind successful.")) {
                    goToLocationActivity();
                }
                else {
                    System.out.println("something went wrong");
                }

                //startActivity(new Intent(getApplicationContext(), LocationActivity.class));
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });
    }

    private void goToLocationActivity () {
        Intent intent = new Intent(this, LocationActivity.class);
        intent.putExtra("associationId", associationId);
        startActivity(intent);
    }


    // qrCode scan using file path emulator
    /*
    public static String scanQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        }
        catch (Exception e) {
            Log.e("QrTest", "Error decoding barcode", e);
        }
        return contents;
    }
    */


}
