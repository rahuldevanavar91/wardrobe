package com.corwdfire.wardrode.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.corwdfire.wardrode.Activity.MainActivity;
import com.corwdfire.wardrode.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by rahul on 11/23/16.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static int NOTIFICATION_ID = 1;

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent targetIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(context.getString(R.string.pant_random_num), intent.getIntExtra(context.getString(R.string.pant_random_num), 0));
        bundle.putInt(context.getString(R.string.shirt_random_num), intent.getIntExtra(context.getString(R.string.shirt_random_num), 0));
        targetIntent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Hi , Good morning")
                .setContentText("New combination of suite available in wardrobe check now.. ")
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
        Log.i("notification", "Notifications sent.");

    }
}
