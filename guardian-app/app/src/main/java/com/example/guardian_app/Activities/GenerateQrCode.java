package com.example.guardian_app.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.Domain.DataStore;
import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.R;
import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQrCode extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewResult;
    private InfoRetreiverApi infoRetreiverApi;
    private DataStore dataStore;

    private String childName;
    private String childCode;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qrcode_activity);
        System.out.println("here");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dataStore = extras.getParcelable("dataStore");
            System.out.println(dataStore.getChildNames());
            childCode = extras.getString("childCode");
        }


        imageView = (ImageView)findViewById(R.id.imageview);
        textViewResult = (TextView)findViewById(R.id.text_view_result);

        textViewResult.setText("Please read this QrCode in your childs Device");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        JsonObject bindRequest = new JsonObject();
        bindRequest.addProperty("publicKey", "publicKeyhuererere");
        bindRequest.addProperty("associationId", childCode);
        System.out.println(bindRequest.toString());

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
        System.out.println(dataStore.getChildNames());
        intent.putExtra("dataStore", dataStore);
        intent.putExtra("childCode", childCode);
        startActivity(intent);
    }



}
