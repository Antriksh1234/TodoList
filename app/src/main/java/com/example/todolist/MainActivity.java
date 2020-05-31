package com.example.todolist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    public void opentask(View view) {
        Intent intent = new Intent(this, TaskActivity2.class);
        startActivity(intent);
    }

    public void showhabit(View view) {
        Intent intent = new Intent(this, HabitActivity2.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.getItem(0);
        sharedPreferences = this.getSharedPreferences("com.example.todolist", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        menuItem.setTitle(username);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.viewprofile:
                finish();
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.contact:
                intent = new Intent(this,ContactActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        sharedPreferences = this.getSharedPreferences("com.example.todolist", MODE_PRIVATE);

        setSupportActionBar(toolbar);
        Button dailyButton = findViewById(R.id.button2);
        Button habitButton = findViewById(R.id.button3);

        dailyButton.setTranslationX(-2500);
        habitButton.setTranslationX(-2500);

        dailyButton.animate().translationXBy(2500).setDuration(700);
        habitButton.animate().translationXBy(2500).setDuration(700);

        Calendar calendar = Calendar.getInstance();
        int anotherday = calendar.get(Calendar.DAY_OF_MONTH);

        if (anotherday != sharedPreferences.getInt("previousopen", 0)) {
            sharedPreferences.edit().putInt("previousopen", anotherday).apply();

            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("habits", MODE_PRIVATE, null);

            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM habits", null);
            int nameIndex = c.getColumnIndex("name");
            int doneIndex = c.getColumnIndex("done");
            int doneTimeIndex = c.getColumnIndex("doneTime");
            int totalTimeIndex = c.getColumnIndex("totalTime");

            c.moveToFirst();

            int last_day_total_habits = 0;

            while (!c.isAfterLast()) {
                int totalTime = c.getInt(totalTimeIndex);
                String name = c.getString(nameIndex);
                totalTime+=1;
                last_day_total_habits++;
                sqLiteDatabase.execSQL("UPDATE habits set totalTime = " + totalTime + " WHERE name ='" + name + "'");
                c.moveToNext();
            }
            sqLiteDatabase.execSQL("UPDATE habits set done = 'No'");

            int no_of_days = sharedPreferences.getInt("days", 0);
            int overallhabits = sharedPreferences.getInt("overallhabits",0);
            overallhabits = overallhabits + last_day_total_habits;
            sharedPreferences.edit().putInt("overallhabits",overallhabits).apply();
            no_of_days++;
            sharedPreferences.edit().putInt("days", no_of_days).apply();

            if (no_of_days >= 8) {

                for(int i = 2; i < 8; i++)
                {
                    sharedPreferences.edit().putInt(i-1+"donehabit",sharedPreferences.getInt(i+"donehabit",0)).apply();
                    sharedPreferences.edit().putInt(i-1+"totalhabit",sharedPreferences.getInt(i+"totalhabit",0)).apply();

                    sharedPreferences.edit().putInt(i-1+"donetask",sharedPreferences.getInt(i+"donetask",0)).apply();
                    sharedPreferences.edit().putInt(i-1+"totaltask",sharedPreferences.getInt(i+"totaltask",0)).apply();
                }
                sharedPreferences.edit().putInt("7totalhabit",last_day_total_habits).apply();
                sharedPreferences.edit().putInt("7donehabit",0).apply();

                sharedPreferences.edit().putInt("7totaltask",0).apply();
                sharedPreferences.edit().putInt("7donetask",0).apply();

            } else {
                sharedPreferences.edit().putInt(no_of_days+"totalhabit",last_day_total_habits).apply();
                sharedPreferences.edit().putInt(no_of_days+"donehabit",0).apply();

                sharedPreferences.edit().putInt(no_of_days+"donetask",0).apply();
                sharedPreferences.edit().putInt(no_of_days+"totaltask",0).apply();

            }
        }

        Intent service = new Intent(this,MyBroadcastReceiver.class);
        this.startService(service);
    }
}
