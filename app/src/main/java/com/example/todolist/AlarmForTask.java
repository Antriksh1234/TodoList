package com.example.todolist;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmForTask extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManagerCompat notificationManager;
        notificationManager = NotificationManagerCompat.from(context);

        Intent intent1 = new Intent(context,TaskActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);

        Notification notification = new NotificationCompat.Builder(context,ChannelCreater.REMINDER_FOR_TASK)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.todolisticon)
                .setContentTitle("Deadline coming close!")
                .setContentText("There is a task to be done in less  than 2 hours to go check that out!")
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build();

         notificationManager.notify(2,notification);
    }
}
