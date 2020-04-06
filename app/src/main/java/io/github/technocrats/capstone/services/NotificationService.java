package io.github.technocrats.capstone.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import io.github.technocrats.capstone.CalendarRecommendation;
import io.github.technocrats.capstone.CapstoneApp;
import io.github.technocrats.capstone.R;

public class NotificationService extends Service {

    private CapstoneApp app;
    private Timer timer;
    private Context mContext;
    private final String TAG = "Capstone Notification";
    private final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onCreate() {
        Log.d(TAG, "Service created");

        app = (CapstoneApp) getApplication();
        mContext = getApplicationContext();
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound - not used!");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Service destroyed");
        stopTimer();
    }

    private void startTimer() {

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Timer task started");

                // display notification
                sendNotification("Select to view recommended items.");
            }
        };

        timer = new Timer(true);
        /* int delay = 1000 * 60 * 60 * 12;    // send notif every 12 hours
        int interval = 1000 * 60 * 60 * 12;   // send notif every 12 hours */

        // for demo purposes only
        int delay = 1000 * 60 * 3;      // send notif every 3 minutes
        int interval = 1000 * 60 * 3;   // send notif every 3 minutes

        timer.schedule(task, delay, interval);
    }

    private void sendNotification(String text) {

        Intent intent = new Intent(mContext , CalendarRecommendation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,
                NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle("View Recommended Inventory")
                .setContentText(text)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // check android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            // build notification channel if higher than Android Oreo
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        assert mNotificationManager != null;
        mNotificationManager.notify(0, mBuilder.build());
        Log.d(TAG, "Notification built.");
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
