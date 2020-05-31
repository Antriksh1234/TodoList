package com.example.todolist;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


public class FullscreenActivity extends AppCompatActivity {

    private int usesno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("habits",MODE_PRIVATE,null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS habits (name VARCHAR, done VARCHAR,doneTime INTEGER,totalTime INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS task (taskName VARCHAR, dueHour INTEGER, dueMinutes INTEGER,dueDate INTEGER,dueMonth INTEGER,dueYear INTEGER, priority VARCHAR, done VARCHAR)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS image(blob BLOB)");

        setContentView(R.layout.activity_fullscreen);

        ImageView imageView = findViewById(R.id.imageView2);
        TranslateAnimation translation;
        translation = new TranslateAnimation(0f, 0F, -180f, 0f);
        translation.setStartOffset(500);
        translation.setDuration(1200);
        translation.setFillAfter(true);
        translation.setInterpolator(new BounceInterpolator());
        imageView.startAnimation(translation);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.todolist", Context.MODE_PRIVATE);
        usesno = sharedPreferences.getInt("usedno",0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(usesno!=0)
                {
                    finish();
                    Intent intent = new Intent(FullscreenActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else{
                    finish();
                    Intent intent = new Intent(FullscreenActivity.this,AccountActivity.class);
                    startActivity(intent);
                }

            }
        },2500);

    }

}
