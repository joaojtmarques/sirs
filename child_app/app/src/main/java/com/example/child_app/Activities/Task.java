package com.example.child_app.Activities;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Task extends java.util.TimerTask{
    private LocationActivity activity;

    public Task(LocationActivity Activity){
        this.activity = Activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void run(){ this.activity.getCurrentLocation(); }
}
