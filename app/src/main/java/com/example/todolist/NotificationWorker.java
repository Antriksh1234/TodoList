package com.example.todolist;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        SQLiteDatabase sqLiteDatabase = getApplicationContext().openOrCreateDatabase("habits",Context.MODE_PRIVATE,null);

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
        c.close();
        if(totalTask > taskDone)
        {
            NotificationManagerCompat notificationManager;

            notificationManager = NotificationManagerCompat.from(getApplicationContext());

            Intent intent1 = new Intent(getApplicationContext(),TaskActivity2.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent1,0);
            Calendar calendar = Calendar.getInstance();
            Notification notification;
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 21 && calendar.get(Calendar.MINUTE) >= 30)
            {

                Intent intent = new Intent(getApplicationContext(),HabitActivity2.class);
                PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                        .setSmallIcon(R.drawable.todo)
                        .setContentTitle("Done with your habits?")
                        .setContentText("Go and check out which habits you have done today")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(pendingIntent1)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .build();
            }
            else
            {

                //sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS task (taskName VARCHAR, dueHour INTEGER, dueMinutes INTEGER,dueDate INTEGER,dueMonth INTEGER,dueYear INTEGER, priority VARCHAR, done VARCHAR)");

                int dueMinutesIndex = c.getColumnIndex("dueMinutes");
                int dueHourIndex = c.getColumnIndex("dueHour");
                int dueDateIndex = c.getColumnIndex("dueDate");
                int dueMonthIndex = c.getColumnIndex("dueMonth");
                int dueYearIndex = c.getColumnIndex("dueYear");
                int doneStatusIndex = c.getColumnIndex("done");
                boolean hasInNextTwoHour = false;
                int noOfTasksInNextTwoHours = 0;
                Calendar taskDueCalendar = Calendar.getInstance();

                c.moveToFirst();

                while (!c.isAfterLast())
                {
                    int dueDate = c.getInt(dueDateIndex);
                    int dueMonth = c.getInt(dueMonthIndex);
                    int dueYear = c.getInt(dueYearIndex);
                    int dueHour = c.getInt(dueHourIndex);
                    int dueMinutes = c.getInt(dueMinutesIndex);
                    String doneStatus = c.getString(doneStatusIndex);
                    taskDueCalendar.set(Calendar.HOUR_OF_DAY,dueHour);
                    taskDueCalendar.set(Calendar.MINUTE,dueMinutes);
                    taskDueCalendar.set(Calendar.MONTH,dueMonth-1);
                    taskDueCalendar.set(Calendar.DAY_OF_MONTH,dueDate);
                    taskDueCalendar.set(Calendar.YEAR,dueYear);

                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.add(Calendar.HOUR_OF_DAY,2);

                    if (currentCalendar.after(taskDueCalendar) && doneStatus.contentEquals("No"))
                    {
                        noOfTasksInNextTwoHours++;
                        hasInNextTwoHour = true;
                    }

                    c.moveToNext();
                }

                if (hasInNextTwoHour)
                {
                    if (noOfTasksInNextTwoHours > 1)
                    {
                        notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                                .setSmallIcon(R.drawable.todolisticon)
                                .setContentTitle("Activate your flash mode!!")
                                .setContentText("There are tasks to be completed within next two hours, Get them done quickly!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(pendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(1,notification);
                    }
                    else
                    {
                        notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                                .setSmallIcon(R.drawable.todolisticon)
                                .setContentTitle("Deadline coming close!")
                                .setContentText("There is a task to be completed within next two hours, Get the job done quickly!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(pendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(1,notification);
                    }
                }

                notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                        .setSmallIcon(R.drawable.todolisticon)
                        .setContentTitle("Check your todolist")
                        .setContentText("See what you have done and what is left!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setAutoCancel(true)
                        .build();
            }
            notificationManager.notify(1,notification);

        }
        return null;
    }
}
