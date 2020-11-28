package com.example.child_app;

public class Task extends java.util.TimerTask{
    private MainActivity activity;

    public Task(MainActivity Activity){
        this.activity = Activity;
    }

    @Override
    public void run(){
        this.activity.getCurrentLocation();
    }
}
