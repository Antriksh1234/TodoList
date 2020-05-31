package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HabitActivity2 extends AppCompatActivity {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    EditText habitText;
    AlertDialog alertDialog;
    ArrayList<String> habits,habitStatus;
    int doneTaskCount;
    ArrayAdapter arrayAdapter;

    class MyAdapter extends ArrayAdapter
    {

        MyAdapter(@NonNull Context context, int resource, @NonNull List objects)
        {
            super(context, resource, objects);
        }

        //getView() method of ArrayAdapter overrode to set the color of the listView item according tto the status of habit done or not
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            view.setBackgroundResource(R.drawable.buttoncapsule);
            if(habitStatus.get(position).contentEquals("Yes"))
            {
                view.setBackgroundResource(R.drawable.selectedbuttoncapsule);
            }

            return view;
        }
    }

    public void addAHabit(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.addhabit,null);

        alert.setView(view1);

        alertDialog = alert.create();

        habitText = (EditText) view1.findViewById(R.id.habitname);
        Button addNew = (Button) view1.findViewById(R.id.addOne);
        Button cancel = (Button) view1.findViewById(R.id.cancelhabit);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(habitText.getText().length() > 0)
                {
                    if (habits.contains(" "+habitText.getText().toString()))
                    {
                        Toast.makeText(HabitActivity2.this, "The habit already exists", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        sqLiteDatabase.execSQL("INSERT INTO habits(name,done,doneTime,totalTime) VALUES ('"+habitText.getText().toString()+"','No',0,0)");
                        habits.add(" "+habitText.getText().toString());
                        habitStatus.add("No");
                        arrayAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                        Toast.makeText(HabitActivity2.this, "Added", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);

                        int overallhabits = sharedPreferences.getInt("overallhabits",0);

                        overallhabits++;

                        sharedPreferences.edit().putInt("overallhabits",overallhabits).apply();

                        int no_of_days = sharedPreferences.getInt("days",0);
                        if(no_of_days < 7)
                        {
                            int totalhabittoday = sharedPreferences.getInt(no_of_days+"totalhabit",0);
                            ++totalhabittoday;
                            sharedPreferences.edit().putInt(no_of_days+"totalhabit",totalhabittoday).apply();
                        }
                        else
                        {
                            int totalhabittoday = sharedPreferences.getInt("7totalhabit",0);
                            ++totalhabittoday;
                            sharedPreferences.edit().putInt("7totalhabit",totalhabittoday).apply();
                        }
                    }

                }
                else
                {
                    Toast.makeText(HabitActivity2.this, "Please enter the name of habit!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit2);

        listView = findViewById(R.id.habitListView);

        Toolbar toolbar = findViewById(R.id.habit2toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Make new Habits - TodoList");

        SharedPreferences sharedPreferences1 = this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);
        boolean firstTime = sharedPreferences1.getBoolean("HabitInstructions",true);
        if (firstTime)
        {
            //Enqueuing task of notification with WorkManager
            PeriodicWorkRequest saveRequests = new PeriodicWorkRequest.Builder(NotificationWorker.class,1, TimeUnit.HOURS).build();
            WorkManager.getInstance(this).enqueue(saveRequests);

            //Providing the instructions for the first time the person opens up the HabitActivity2
            AlertDialog builder = new AlertDialog.Builder(this)
                    .setTitle("Instructions")
                    .setMessage("1. To add a habit, press the button '+' and accordingly add the habit you want. \n\n2. To mark a habit as done for today tap on the habit you want to mark. However if you accidentally marked it as done, you can again tap on it to unmark.\n\n3. Long press on any habit to delete it.")
                    .setPositiveButton("Ok",null)
                    .setIcon(android.R.drawable.ic_menu_help)
                    .show();

            sharedPreferences1.edit().putBoolean("HabitInstructions",false).apply();
        }

        habits = new ArrayList<>();
        habitStatus = new ArrayList<>();

        sqLiteDatabase = this.openOrCreateDatabase("habits",MODE_PRIVATE,null);

        final SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM habits",null);
        int nameIndex = c.getColumnIndex("name");
        int doneIndex = c.getColumnIndex("done");
        c.moveToFirst();

        habitStatus.clear();
        habits.clear();

        while(!c.isAfterLast())
        {
            habits.add(" "+c.getString(nameIndex));
            if(c.getString(doneIndex).contentEquals("No"))
                habitStatus.add("No");
            else {
                habitStatus.add("Yes");
                doneTaskCount++;
            }
            c.moveToNext();
        }

        arrayAdapter = new HabitActivity2.MyAdapter(this,R.layout.custom_listview,habits);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                if(habitStatus.get(position).contentEquals("No")){

                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM habits WHERE name = '"+habits.get(position).substring(1)+"'",null);
                    int totalTimeIndex = c.getColumnIndex("totalTime");
                    int doneTimeIndex = c.getColumnIndex("doneTime");
                    c.moveToFirst();

                    while (!c.isAfterLast())
                    {
                        if(c.getInt(totalTimeIndex) == 0)
                        {
                            sqLiteDatabase.execSQL("UPDATE habits set totalTime = 1 WHERE name = '"+habits.get(position).substring(1)+"'");
                        }
                        int doneTime = c.getInt(doneTimeIndex);
                        doneTime++;
                        sqLiteDatabase.execSQL("UPDATE habits set doneTime = "+doneTime+" WHERE name = '"+habits.get(position).substring(1)+"'");
                        c.moveToNext();
                    }

                    listView.getChildAt(position).setBackgroundResource(R.drawable.selectedbuttoncapsule);
                    habitStatus.set(position,"Yes");
                    doneTaskCount++;
                    sqLiteDatabase.execSQL("UPDATE habits set done = 'Yes' WHERE name = '"+habits.get(position).substring(1)+"'");
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(HabitActivity2.this, "Marked as done", Toast.LENGTH_SHORT).show();

                    int no_of_days = sharedPreferences.getInt("days",0);

                    int overalldonehabits = sharedPreferences.getInt("overalldonehabits",0);
                    overalldonehabits++;
                    sharedPreferences.edit().putInt("overalldonehabits",overalldonehabits).apply();

                    if(no_of_days < 7)
                    {
                        int totalhabitdonetoday = sharedPreferences.getInt(no_of_days+"donehabit",0);
                        ++totalhabitdonetoday;
                        sharedPreferences.edit().putInt(no_of_days+"donehabit",totalhabitdonetoday).apply();
                    }
                    else
                    {
                        int totalhabitdonetoday = sharedPreferences.getInt("7donehabit",0);
                        ++totalhabitdonetoday;
                        sharedPreferences.edit().putInt("7donehabit",totalhabitdonetoday).apply();
                    }

                }
                else
                {
                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM habits WHERE name = '"+habits.get(position).substring(1)+"'",null);
                    int doneTimeIndex = c.getColumnIndex("doneTime");
                    c.moveToFirst();

                    while (!c.isAfterLast())
                    {
                        int doneTime = c.getInt(doneTimeIndex);
                        doneTime--;
                        sqLiteDatabase.execSQL("UPDATE habits set doneTime = "+doneTime+" WHERE name = '"+habits.get(position).substring(1)+"'");
                        c.moveToNext();
                    }

                    int no_of_days = sharedPreferences.getInt("days",0);
                    int overalldonehabits = sharedPreferences.getInt("overalldonehabits",0);
                    overalldonehabits--;
                    sharedPreferences.edit().putInt("overalldonehabits",overalldonehabits).apply();
                    if(no_of_days < 7)
                    {
                        int totalhabitdonetoday = sharedPreferences.getInt(no_of_days+"donehabit",0);
                        --totalhabitdonetoday;
                        sharedPreferences.edit().putInt(no_of_days+"donehabit",totalhabitdonetoday).apply();
                    }
                    else
                    {
                        int totalhabitdonetoday = sharedPreferences.getInt("7donehabit",0);
                        --totalhabitdonetoday;
                        sharedPreferences.edit().putInt("7donehabit",totalhabitdonetoday).apply();
                    }

                    habitStatus.set(position,"No");
                    doneTaskCount--;
                    listView.getChildAt(position).setBackgroundResource(R.drawable.buttoncapsule);
                    sqLiteDatabase.execSQL("UPDATE habits set done = 'No' WHERE name = '"+habits.get(position).substring(1)+"'");
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(HabitActivity2.this, "Marked as not done", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //OnItemLongClickListener() for listView to open the habit's description in HabitDescription activity
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                sqLiteDatabase.execSQL("DELETE FROM habits WHERE name = '"+habits.get(pos).substring(1)+"'");
                String nameOfHabit = habits.get(pos);
                habits.remove(pos);
                habitStatus.remove(pos);
                arrayAdapter.notifyDataSetChanged();

                Toast.makeText(HabitActivity2.this, "Deleted "+nameOfHabit+" from your list", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }
}
