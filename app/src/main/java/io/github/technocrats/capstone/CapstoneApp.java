package io.github.technocrats.capstone;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import io.github.technocrats.capstone.services.NotificationService;

public class CapstoneApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CapstoneApp", "App started");

        // call method to start service
        startNotificationService();
    }

    private void startNotificationService() {
        // start service
        Intent service = new Intent(this, NotificationService.class);
        startService(service);
    }
}
