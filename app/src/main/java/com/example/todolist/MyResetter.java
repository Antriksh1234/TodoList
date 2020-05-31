package com.example.todolist;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyResetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


      SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("habits",Context.MODE_PRIVATE,null);

      int taskDone = 0;
      int totalTask = 0;

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM task",null);

        int doneIndex = c.getColumnIndex("done");

        c.moveToFirst();

        while (!c.isAfterLast())
        {
            if(c.getString(doneIndex).contentEquals("Yes"))
                taskDone++;
            totalTask++;
            c.moveToNext();
        }

        if(totalTask > taskDone)
        {
            NotificationManagerCompat notificationManager;

            notificationManager = NotificationManagerCompat.from(context);

            Intent intent1 = new Intent(context,TaskActivity2.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);

            Notification notification = new  NotificationCompat.Builder(context,ChannelCreater.ABOUT_TASK_LEFT)
                    .setSmallIcon(R.drawable.todo)
                    .setContentTitle("Some tasks are left")
                    .setContentText("Quickly go and get the job done right now")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(1,notification);

        }

    }
}
