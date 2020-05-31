package com.example.todolist;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

public class ChannelCreater extends Application {
    public static final String ABOUT_TASK_LEFT= "taskLeft";
    public static final String REMINDER_FOR_TASK = "taskreminderfordeadeline";
    public static final String REMINDER_FOR_HABITS = "habitreminderforday";
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    public void createNotificationChannels()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(
                    ABOUT_TASK_LEFT,
                    "taskTeller",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This notification tells about left task if any");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

            NotificationChannel channel2 = new NotificationChannel(
                    REMINDER_FOR_TASK,
                    "reminder for task 2 hours ago",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("Remids about the tasks and alarms the system 2 hours go");

            manager.createNotificationChannel(channel2);

            NotificationChannel channel3 = new NotificationChannel(
                    REMINDER_FOR_HABITS,
                    "reminder for habits at 9;30 pm",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel3.setDescription("This reminds about the habits to be done on that day at 9:30 pm");
            manager.createNotificationChannel(channel3);
        }
    }
}
