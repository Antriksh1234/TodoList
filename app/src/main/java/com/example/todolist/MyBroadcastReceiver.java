package com.example.todolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class MyBroadcastReceiver extends Service {
    private static BroadcastReceiver br_ScreenOffReceiver;
    private boolean forHabits = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent4 = new Intent(this, MyBroadcastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent4, 0);
        if (calendar.after(Calendar.getInstance()))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(),pendingIntent),pendingIntent);
            }
            else
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        }

        //Setting general alarm for tasks at 9:00 morning
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 9);
        calendar1.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager2 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent2 = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent2, 0);

        if (calendar1.after(Calendar.getInstance()))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager2.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar1.getTimeInMillis(),pendingIntent2),pendingIntent2);
            }
            else
                alarmManager2.set(AlarmManager.RTC, calendar1.getTimeInMillis(), pendingIntent2);
        }
        else
            calendar1.add(Calendar.DAY_OF_MONTH,1);


        //Setting general alarm for tasks at 4:00 pm
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 16);
        calendar2.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager3 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent3 = new Intent(this, MyResetter.class);
        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(this, 0, intent3, 0);
        if(calendar2.getTimeInMillis() >  Calendar.getInstance().getTimeInMillis())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alarmManager3.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar1.getTimeInMillis(),pendingIntent3),pendingIntent3);
            }
            else
                alarmManager3.set(AlarmManager.RTC,calendar2.getTimeInMillis(),pendingIntent3);
        }

        //Setting alarm for habits at 9:30 pm
        Calendar habitAlarmCalendar = Calendar.getInstance();
        habitAlarmCalendar.set(Calendar.HOUR_OF_DAY,21);
        habitAlarmCalendar.set(Calendar.MINUTE, 30);

        AlarmManager alarmManager1 = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(this,AlarmForHabits.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this,0,intent1,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            alarmManager2.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar1.getTimeInMillis(),pendingIntent2),pendingIntent2);
        }
        else
            alarmManager1.set(AlarmManager.RTC,habitAlarmCalendar.getTimeInMillis(),pendingIntent1);

        ComponentName receiver1 = new ComponentName(this, MyResetter.class);
        PackageManager pm1 = this.getPackageManager();

        pm1.setComponentEnabledSetting(receiver1,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        ComponentName receiver = new ComponentName(this, AlarmForTask.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        ComponentName receiver2 = new ComponentName(this, AlarmForHabits.class);
        PackageManager pm2 = this.getPackageManager();

        pm2.setComponentEnabledSetting(receiver2,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        registerScreenOffReceiver();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(br_ScreenOffReceiver);
        br_ScreenOffReceiver = null;
    }

    private void registerScreenOffReceiver()
    {
        br_ScreenOffReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                // do something, e.g. send Intent to main app
                NotificationManagerCompat notificationManager;

                notificationManager = NotificationManagerCompat.from(getApplicationContext());

                Intent intent1 = new Intent(getApplicationContext(),TaskActivity2.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent1,0);

                Notification notification;
                Calendar calendar = Calendar.getInstance();
                if (calendar.get(Calendar.HOUR_OF_DAY) >= 9 && calendar.get(Calendar.MINUTE) == 0)
                {
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Check out your list of tasks!")
                            .setContentText("Is anything left? Or Wanna remove some tasks for whom you are sureshot are done forever?")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }

                    SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("habits",MODE_PRIVATE,null);
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

                    if (taskDone  < totalTask)
                    {
                        notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                                .setSmallIcon(R.drawable.todolisticon)
                                .setContentTitle("Have a look at your todolist")
                                .setContentText("See your pending tasks right now before its too late!")
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
                                .setContentTitle("Have a look at your todolist")
                                .setContentText("Having an idea about your current position throughout the day is a good practice!")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(pendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(1,notification);
                    }

                    sqLiteDatabase = openOrCreateDatabase("habits",MODE_PRIVATE,null);
                    taskDone = 0;
                    totalTask = 0;

                    c = sqLiteDatabase.rawQuery("SELECT * FROM task",null);

                    doneIndex = c.getColumnIndex("done");

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
                        notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                                .setSmallIcon(R.drawable.todolisticon)
                                .setContentTitle("Have a look at your todolist")
                                .setContentText("See your pending tasks")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_ALARM)
                                .setContentIntent(pendingIntent)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(1,notification);
                    }

                if (calendar.get(Calendar.HOUR_OF_DAY) == 21 && calendar.get(Calendar.MINUTE) >= 15 && calendar.get(Calendar.MINUTE) < 30)
                {
                    Intent intent2 = new Intent(getApplicationContext(),HabitActivity2.class);
                    PendingIntent pendingIntent1 = PendingIntent.getBroadcast(getApplicationContext(),0,intent2,0);
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Are you done with your habits?")
                            .setContentText("See what habits are needed to be done and what not")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent1)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }

                if (calendar.get(Calendar.HOUR_OF_DAY) == 19 && calendar.get(Calendar.MINUTE) >= 50)
                {
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Check your todolist")
                            .setContentText("Is anything left? Are there any tasks you want to delete forever?")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }

                if (calendar.get(Calendar.HOUR_OF_DAY) == 14 && calendar.get(Calendar.MINUTE) >= 30)
                {
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Check your todolist")
                            .setContentText("Is anything left? Are there any tasks you want to delete forever?")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }


                if (calendar.get(Calendar.MINUTE) % 7 == 0)
                {
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Check out your todolist")
                            .setContentText("Is anything left? Are there any tasks you want to remove forever?")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }

                if (calendar.get(Calendar.HOUR_OF_DAY) == 7 && calendar.get(Calendar.MINUTE) < 10)
                {
                    notification = new  NotificationCompat.Builder(getApplicationContext(),ChannelCreater.ABOUT_TASK_LEFT)
                            .setSmallIcon(R.drawable.todolisticon)
                            .setContentTitle("Check your todolist")
                            .setContentText("Is anything left? Are there any tasks you want to delete forever?")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_ALARM)
                            .setContentIntent(pendingIntent)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true)
                            .build();

                    notificationManager.notify(1,notification);
                }

                sqLiteDatabase = openOrCreateDatabase("habits",MODE_PRIVATE,null);

                c = sqLiteDatabase.rawQuery("SELECT * FROM task",null);

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
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(br_ScreenOffReceiver, filter);
    }

}
