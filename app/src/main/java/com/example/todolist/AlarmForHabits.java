package com.example.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmForHabits extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

       /* Intent intent1 = new Intent(context, MyNewIntentService.class);
        context.startService(intent1);*/
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("habits",Context.MODE_PRIVATE,null);

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM habits",null);

        int totalHabits = 0;
        int doneHabits = 0;
        int doneIndex = c.getColumnIndex("done");
        c.moveToFirst();

        while (!c.isAfterLast())
        {
            if (c.getString(doneIndex).contentEquals("Yes"))
                doneHabits++;
            totalHabits++;
        }

        if (doneHabits < totalHabits)
        {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            Intent intent1 = new Intent(context,HabitActivity2.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent1,0);

            Notification notification = new NotificationCompat.Builder(context,ChannelCreater.REMINDER_FOR_HABITS)
                    .setContentTitle("Are you done with your habits you wanted to perform?")
                    .setContentText("Seems like there are some habits that you haven't done them yet")
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.todolisticon)
                    .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                    .build();

            notificationManager.notify(3,notification);
        }
    }
}
