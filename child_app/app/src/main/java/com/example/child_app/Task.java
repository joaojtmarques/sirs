package com.example.child_app;

public class Task extends java.util.TimerTask{
    private LocationActivity activity;

    public Task(LocationActivity Activity){
        this.activity = Activity;
    }

    @Override
    public void run(){
        this.activity.getCurrentLocation();
    }
}
