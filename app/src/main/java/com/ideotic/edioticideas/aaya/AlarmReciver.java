package com.ideotic.edioticideas.aaya;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Shubham on 15-05-2016.
 */
public class AlarmReciver extends BroadcastReceiver {
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        SingleRowReminder sr=null;
        DataBase db = new DataBase(context);
        try {
            db.Open();
             sr= db.getROW(intent.getStringExtra("id"));
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        NotificationCompat.Builder mBuilder =

                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(sr.tittle)
                        .setContentText(sr.des);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        // The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(0,mBuilder.build());


        MediaPlayer m = new MediaPlayer();
        AssetFileDescriptor f  =null;
        try {
             f= context.getAssets().openFd("ringtone.mp3");
             m.setDataSource(f.getFileDescriptor());
            m.prepare();
            m.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
