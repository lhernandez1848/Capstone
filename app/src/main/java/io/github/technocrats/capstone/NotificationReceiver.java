package io.github.technocrats.capstone;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private Context mContext;
    private final String TAG = "Capstone Notification";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {

        // initialize global variable
        mContext = context;

        // all createNotification method
        createNotification();
    }

    private void createNotification() {
        Log.d(TAG, "Creating notification...");

        // declare and initialize notification details
        String title = "View Recommended Inventory";
        String text = "Select to view recommendations.";

        // get current date
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;

        Intent intent = new Intent(mContext , CalendarRecommendation.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // save current date to intent
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,
                NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(R.drawable.ic_notification);
        mBuilder.setContentTitle(title)
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
}
