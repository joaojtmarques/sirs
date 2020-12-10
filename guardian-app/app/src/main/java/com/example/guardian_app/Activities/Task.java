package com.example.guardian_app.Activities;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class Task extends java.util.TimerTask{
    private MainActivity _activity;

    public Task(MainActivity activity){
        this._activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void run(){ this._activity.getEveryChildLocation(); }
}