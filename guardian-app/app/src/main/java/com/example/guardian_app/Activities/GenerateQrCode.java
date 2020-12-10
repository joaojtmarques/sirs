package com.example.guardian_app.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.SecretKey;

public class GenerateQrCode extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewResult;
    private DataStore dataStore;

    private String childCode;
    private String publicKey;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qrcode_activity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            childCode = extras.getString("childCode");
            publicKey = extras.getString("publicKey");
        }


        imageView = (ImageView)findViewById(R.id.imageview);
        textViewResult = (TextView)findViewById(R.id.text_view_result);

        textViewResult.setText("Please read this QrCode in your childs Device");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        JsonObject bindRequest = new JsonObject();
        bindRequest.addProperty("publicKey", publicKey);
        bindRequest.addProperty("associationId", childCode);

        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(bindRequest.toString(), BarcodeFormat.QR_CODE,500,500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void goToChildAdded (View view){
        Intent intent = new Intent(this, ChildAdded.class);
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("childCode", childCode);
        startActivity(intent);
    }



}
