package com.example.child_app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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





public class ReadQrCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate", "on Create!!!");

        setContentView(R.layout.read_qrcode_activity);

        System.out.println("WHATS GOOD CARALHO");
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




    }

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


}
