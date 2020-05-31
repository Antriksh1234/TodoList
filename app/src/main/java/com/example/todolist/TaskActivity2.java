package com.example.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TaskActivity2 extends AppCompatActivity {


    private ListView listView;
    TextView titleText,subtitleText;
    static TaskActivity2.MyAdapter arrayAdapter;
    ArrayList<String> done = new ArrayList<>();
    private SQLiteDatabase sqLiteDatabase;
    ArrayList<String> taskList = new ArrayList<>();
    ArrayList<String> dueList = new ArrayList<>();
    ArrayList<String> priority = new ArrayList<>();

    class MyAdapter extends ArrayAdapter {

        Context context;
        ArrayList<String> taskList;
        ArrayList<String> dueList;
        ArrayList<String> priorityList;

        //Constructor to initialize with the text
        public MyAdapter(Activity context, ArrayList<String> maintitle, ArrayList<String> subtitle, ArrayList<String> priority) {
            super(context, R.layout.custom_taskview, maintitle);
            this.context = context;
            taskList = maintitle;
            dueList = subtitle;
            priorityList = priority;
        }

        //To customize the listView items
        public View getView(int position, View view, ViewGroup parent) {

            View rowView = getLayoutInflater().inflate(R.layout.custom_taskview, null,true);

            titleText = (TextView) rowView.findViewById(R.id.taskname);
            subtitleText = (TextView) rowView.findViewById(R.id.duetime);
            ImageView showPriorityLevel = (ImageView) rowView.findViewById(R.id.priorityView);
            titleText.setText(taskList.get(position));
            subtitleText.setText(dueList.get(position));

            if(priorityList.get(position).contentEquals("High"))
                showPriorityLevel.setImageResource(android.R.drawable.btn_star_big_on);
            else
                showPriorityLevel.setImageResource(android.R.drawable.btn_star_big_off);

            if(done.get(position).contentEquals("Yes"))
            {
                rowView.setBackgroundResource(R.drawable.imageselected);
                titleText.setBackgroundResource(R.drawable.titleheaderdselected);
                subtitleText.setBackgroundResource(R.drawable.subtitleselected);
            }
            else
            {
                rowView.setBackgroundResource(R.drawable.imagenotselected);
                titleText.setBackgroundResource(R.drawable.titleheadernotselected);
                subtitleText.setBackgroundResource(R.drawable.subtitlenotselected);
            }
            return rowView;
        }
    }

    public void addtask(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view1 = getLayoutInflater().inflate(R.layout.taskadder,null);

        builder.setView(view1);

        final EditText taskname = view1.findViewById(R.id.editText2);
        Button addtaskbtn = view1.findViewById(R.id.addtask);
        Button canceladdtask = view1.findViewById(R.id.canceladdtask);

        final TimePicker timePicker = view1.findViewById(R.id.timepicker);
        final DatePicker datePicker = view1.findViewById(R.id.datepicker);
        timePicker.setIs24HourView(true);
        final AlertDialog alertDialog = builder.create();
        canceladdtask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        addtaskbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                RadioGroup radioGroup = view1.findViewById(R.id.radiogrp);

                int radioId = radioGroup.getCheckedRadioButtonId();

                String priorityText;

                if(radioId == R.id.lowpriority)
                {
                    priorityText = "Low";
                }
                else
                {
                    priorityText = "High";
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    //For android version MarshMallow and higher

                    int hour = timePicker.getHour();
                    int min =  timePicker.getMinute();
                    int date = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth()+1;
                    int year = datePicker.getYear();
                    if(taskname.getText().length() > 0)
                    {

                        String time;

                        String subtitle = "";

                        Calendar calendar = Calendar.getInstance();

                        if(calendar.get(Calendar.YEAR)== year && calendar.get(Calendar.MONTH)+1 == month &&  calendar.get(Calendar.DAY_OF_MONTH) == date )
                        {
                            if(hour > calendar.get(Calendar.HOUR_OF_DAY))
                                time = "due today ";
                            else if(hour == calendar.get(Calendar.HOUR_OF_DAY) && min >= calendar.get(Calendar.MINUTE))
                                time = "due today ";
                            else
                                time = "Overdue today";
                        }
                        else if(calendar.get(Calendar.YEAR) < year)
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.MONTH)+1 < month)
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.YEAR) > year)
                        {
                            time = "Overdue ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.MONTH)+1 > month)
                        {
                            time = "Overdue ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.DAY_OF_MONTH) < date)
                        {
                            if(calendar.get(Calendar.DAY_OF_MONTH) == date-1)
                                time = "due tommorrow ";
                            else {
                                time = "due ";
                                time += date+"/"+month+"/"+year+" ";
                            }
                        }
                        else if(calendar.get(Calendar.DAY_OF_MONTH) > date)
                        {
                            if(calendar.get(Calendar.DAY_OF_MONTH) == date+1)
                                time = "Overdue yesterday ";
                            else {
                                time = "Overdue ";
                                time += date+"/"+month+"/"+year+" ";
                            }
                        }
                        else
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        String minutes;
                        if(min < 10)
                        {
                            minutes = "0"+min;
                        }
                        else
                            minutes = Integer.toString(min);

                        if(hour > 12)
                        {
                            minutes+=" pm";
                            subtitle = time + hour%12 + ":" + minutes;
                            //dueList.add(time + hour%12 + ":" + minutes );
                        }
                        else if(hour == 12)
                        {
                            minutes+=" pm";
                            subtitle = time + hour + ":" + minutes;
                           // dueList.add(time + hour + ":" + minutes );
                        }
                        else {
                            minutes += " am";
                            subtitle = time + hour + ":" + minutes;
                           // dueList.add(time + hour + ":" + minutes );
                        }
                        done.add("No");

                        if (taskList.contains(taskname.getText().toString()))
                        {
                            //Same task already exists
                            Toast.makeText(TaskActivity2.this, "The exact same task exists already", Toast.LENGTH_SHORT).show();
                        }
                        else    //Task is new and could be added
                        {
                            sqLiteDatabase.execSQL("INSERT INTO task (taskName,dueHour,dueMinutes,dueDate,dueMonth,dueYear,priority,done) VALUES ('"+taskname.getText().toString()+"',"+hour+","+min+","+date+","+month+","+year+",'"+priorityText+"','No')");
                            taskList.add(taskname.getText().toString());
                            priority.add(priorityText);
                            dueList.add(subtitle);
                            arrayAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();

                            Calendar taskAlarmCalendar = Calendar.getInstance();

                            taskAlarmCalendar.set(Calendar.DAY_OF_MONTH,date);
                            taskAlarmCalendar.set(Calendar.HOUR_OF_DAY,hour);
                            taskAlarmCalendar.set(Calendar.YEAR,year);
                            taskAlarmCalendar.set(Calendar.MINUTE,min);
                            taskAlarmCalendar.set(Calendar.MONTH,month-1);

                            taskAlarmCalendar.add(Calendar.HOUR_OF_DAY,-2);

                            if (calendar.after(taskAlarmCalendar))
                            {
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(TaskActivity2.this,AlarmForTask.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskActivity2.this,0,intent,0);
                                alarmManager.set(AlarmManager.RTC,taskAlarmCalendar.getTimeInMillis(),pendingIntent);
                            }
                        }

                    }
                    else
                    {
                        Toast.makeText(TaskActivity2.this, "Please enter the task name!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //For android version lower than MarshMallow
                    if(taskname.getText().length() > 0) {
                        int hour = timePicker.getCurrentHour();
                        int min = timePicker.getCurrentMinute();
                        int date = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth();
                        int year = datePicker.getYear();
                        Calendar calendar= Calendar.getInstance();

                        String time;

                        if(calendar.get(Calendar.YEAR)== year && calendar.get(Calendar.MONTH)+1 == month &&  calendar.get(Calendar.DAY_OF_MONTH) == date )
                        {
                            if(hour > calendar.get(Calendar.HOUR_OF_DAY))
                                time = "due today ";
                            else if(hour == calendar.get(Calendar.HOUR_OF_DAY) && min >= calendar.get(Calendar.MINUTE))
                                time = "due today ";
                            else
                                time = "Overdue today ";
                        }
                        else if(calendar.get(Calendar.YEAR) < year)
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.MONTH)+1 < month)
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.YEAR) > year)
                        {
                            time = "Overdue ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.MONTH)+1 > month)
                        {
                            time = "Overdue ";
                            time += date+"/"+month+"/"+year+" ";
                        }
                        else if(calendar.get(Calendar.DAY_OF_MONTH) < date)
                        {
                            if(calendar.get(Calendar.DAY_OF_MONTH) == date-1)
                                time = "due tommorrow ";
                            else {
                                time = "due ";
                                time += date+"/"+month+"/"+year+" ";
                            }
                        }
                        else if(calendar.get(Calendar.DAY_OF_MONTH) > date)
                        {
                            if(calendar.get(Calendar.DAY_OF_MONTH) == date+1)
                                time = "Overdue yesterday ";
                            else {
                                time = "Overdue ";
                                time += date+"/"+month+"/"+year+" ";
                            }
                        }
                        else
                        {
                            time = "due ";
                            time += date+"/"+month+"/"+year+" ";
                        }

                        sqLiteDatabase.execSQL("INSERT INTO task (taskName,dueHour,dueMinutes,dueDate,dueMonth,dueYear,priority,done) VALUES ('" + taskname.getText().toString() + "'," + hour + "," + min + "," + date + "," + month + "," + year + ",'" + priorityText + "','No')");
                        taskList.add(taskname.getText().toString());
                        priority.add(priorityText);

                        String minutes;
                        if(min < 10)
                        {
                            minutes = "0"+min;
                        }
                        else
                            minutes = Integer.toString(min);

                        if(hour > 12)
                        {
                            minutes+=" pm";
                            dueList.add(time + hour%12 + ":" + minutes );
                        }
                        else if(hour == 12)
                        {
                            minutes+=" pm";
                            dueList.add(time + hour + ":" + minutes );
                        }
                        else {
                            minutes += " am";
                            dueList.add(time + hour + ":" + minutes );
                        }
                        done.add("No");
                        arrayAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();

                        Calendar taskAlarmCalendar = Calendar.getInstance();

                        taskAlarmCalendar.set(Calendar.DAY_OF_MONTH,date);
                        taskAlarmCalendar.set(Calendar.HOUR_OF_DAY,hour);
                        taskAlarmCalendar.set(Calendar.YEAR,year);
                        taskAlarmCalendar.set(Calendar.MINUTE,min);
                        taskAlarmCalendar.set(Calendar.MONTH,month-1);

                        taskAlarmCalendar.add(Calendar.HOUR_OF_DAY,-2);
                        if (calendar.after(taskAlarmCalendar))
                        {
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent intent = new Intent(TaskActivity2.this,AlarmForTask.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(TaskActivity2.this,0,intent,0);
                            alarmManager.set(AlarmManager.RTC,taskAlarmCalendar.getTimeInMillis(),pendingIntent);
                        }

                    }
                    else
                    {
                        Toast.makeText(TaskActivity2.this, "Please enter the task name", Toast.LENGTH_SHORT).show();
                    }
                }

                SharedPreferences sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);
                int no_of_days = sharedPreferences.getInt("days",0);
                if(no_of_days < 8)
                {
                    int todaytasktotal;
                    todaytasktotal = sharedPreferences.getInt(no_of_days+"totaltask",0);
                    sharedPreferences.edit().putInt(no_of_days+"totaltask",++todaytasktotal).apply();
                }
                else
                {
                    int todaytasktotal = sharedPreferences.getInt("7totaltask",0);
                    sharedPreferences.edit().putInt("7totaltask",++todaytasktotal).apply();
                }

                int overalltotaltask = sharedPreferences.getInt("overalltask",0);
                overalltotaltask++;
                sharedPreferences.edit().putInt("overalltask",overalltotaltask).apply();
            }
        });

        alertDialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task2);

        listView = findViewById(R.id.tasklistView);

        Toolbar toolbar = findViewById(R.id.task2toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage your daily activity - TodoList");

        SharedPreferences sharedPreferences1 = this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);
        boolean firstTime = sharedPreferences1.getBoolean("TaskInstructions",true);
        if (firstTime)
        {
            //Enqueuing the task of notification with the WorkManager
            PeriodicWorkRequest saveRequests = new PeriodicWorkRequest.Builder(NotificationWorker.class,1, TimeUnit.HOURS).build();
            WorkManager.getInstance(this).enqueue(saveRequests);

            //Providing instructions for the first time
            AlertDialog builder = new AlertDialog.Builder(this)
                    .setTitle("Instructions")
                    .setMessage("1. To add a task, press the button '+' and accordingly add the task you want. \n\n2. To mark a task as done, tap on the task you want to mark. However if you accidentally marked it as done you can again tap on it to unmark.\n\n3. Long press on any task to delete it.")
                    .setPositiveButton("Ok",null)
                    .setIcon(android.R.drawable.ic_menu_help)
                    .show();

            sharedPreferences1.edit().putBoolean("TaskInstructions",false).apply();
        }

        sqLiteDatabase = this.openOrCreateDatabase("habits",MODE_PRIVATE,null);

        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM task",null);

        int tasknameIndex = c.getColumnIndex("taskName");
        int dueHourIndex = c.getColumnIndex("dueHour");
        int dueMinutesIndex = c.getColumnIndex("dueMinutes");
        int dueDate = c.getColumnIndex("dueDate");
        int dueMonth = c.getColumnIndex("dueMonth");
        int dueYear = c.getColumnIndex("dueYear");
        final int priorityIndex = c.getColumnIndex("priority");
        int doneIndex = c.getColumnIndex("done");


        taskList = new ArrayList<>();
        dueList = new ArrayList<>();
        priority = new ArrayList<>();
        done = new ArrayList<>();
        c.moveToFirst();

        while (!c.isAfterLast())
        {
            taskList.add(c.getString(tasknameIndex));
            priority.add(c.getString(priorityIndex));
            String time;
            int date = c.getInt(dueDate);
            int month = c.getInt(dueMonth);
            int year = c.getInt(dueYear);
            int hour = c.getInt(dueHourIndex);
            int min = c.getInt(dueMinutesIndex);
            Calendar calendar = Calendar.getInstance();

            if(calendar.get(Calendar.YEAR)== year && calendar.get(Calendar.MONTH)+1 == month &&  calendar.get(Calendar.DAY_OF_MONTH) == date )
            {
                if(hour > calendar.get(Calendar.HOUR_OF_DAY))
                    time = "due today ";
                else if(hour == calendar.get(Calendar.HOUR_OF_DAY) && min >= calendar.get(Calendar.MINUTE))
                    time = "due today ";
                else
                    time = "Overdue today ";
            }
            else if(calendar.get(Calendar.YEAR) < year)
            {
                time = "due ";
                time += date+"/"+month+"/"+year+" ";
            }
            else if(calendar.get(Calendar.MONTH)+1 < month)
            {
                time = "due ";
                time += date+"/"+month+"/"+year+" ";
            }
            else if(calendar.get(Calendar.YEAR) > year)
            {
                time = "Overdue ";
                time += date+"/"+month+"/"+year+" ";
            }
            else if(calendar.get(Calendar.MONTH)+1 > month)
            {
                time = "Overdue ";
                time += date+"/"+month+"/"+year+" ";
            }
            else if(calendar.get(Calendar.DAY_OF_MONTH) < date)
            {
                if(calendar.get(Calendar.DAY_OF_MONTH) == date-1)
                    time = "due tommorrow ";
                else {
                    time = "due ";
                    time += date+"/"+month+"/"+year+" ";
                }
            }
            else if(calendar.get(Calendar.DAY_OF_MONTH) > date)
            {
                if(calendar.get(Calendar.DAY_OF_MONTH) == date+1)
                    time = "Overdue yesterday ";
                else {
                    time = "Overdue ";
                    time += date+"/"+month+"/"+year+" ";
                }
            }
            else
            {
                time = "due ";
                time += date+"/"+month+"/"+year+" ";
            }
            String minutes;
            if(c.getInt(dueMinutesIndex) < 10)
            {
                minutes = "0"+c.getInt(dueMinutesIndex);
            }
            else
                minutes = Integer.toString(c.getInt(dueMinutesIndex));

            if(c.getInt(dueHourIndex) > 12)
            {
                minutes+=" pm";
                dueList.add(time + c.getInt(dueHourIndex)%12 + ":" + minutes );
            }

            else {
                minutes += " am";
                dueList.add(time + c.getInt(dueHourIndex) + ":" + minutes );
            }
            if(c.getString(doneIndex).contentEquals("Yes"))
            {
                done.add("Yes");
            }
            else
            {
                done.add("No");
            }
            c.moveToNext();
        }

        arrayAdapter = new MyAdapter(this,taskList,dueList,priority);
        listView.setAdapter(arrayAdapter);

        //Change the status to be marked as done or not done by clicking on listView items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(done.get(position).contentEquals("Yes"))
                {
                    titleText.setBackgroundResource(R.drawable.titleheadernotselected);
                    subtitleText.setBackgroundResource(R.drawable.subtitlenotselected);
                    done.set(position,"No");
                    listView.getChildAt(position).setBackgroundResource(R.drawable.imagenotselected);
                    sqLiteDatabase.execSQL("UPDATE task set done = 'No' WHERE taskName = '"+taskList.get(position)+"'");
                    sqLiteDatabase.execSQL("UPDATE task set taskName = '"+taskList.get(position).substring(0,taskList.get(position).indexOf('\t'))+"' WHERE taskName = '"+taskList.get(position)+"'");
                    taskList.set(position,taskList.get(position).substring(0,taskList.get(position).indexOf('\t')));
                    Toast.makeText(TaskActivity2.this, "Marked as not done", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);
                    int no_of_days = sharedPreferences.getInt("days",0);

                    if(no_of_days <= 7)
                    {
                        int totaltaskdonetoday = sharedPreferences.getInt(no_of_days+"donetask",0);
                        sharedPreferences.edit().putInt(no_of_days+"donetask",--totaltaskdonetoday).apply();
                    }
                    else
                    {
                        int totaltaskdonetoday = sharedPreferences.getInt("7donetask",0);
                        sharedPreferences.edit().putInt("7donetask",--totaltaskdonetoday).apply();
                    }

                    arrayAdapter.notifyDataSetChanged();

                    int overalldonetask = sharedPreferences.getInt("overalltaskdone",0);

                    if (overalldonetask > 0)
                        overalldonetask--;
                    sharedPreferences.edit().putInt("overalltaskdone",overalldonetask).apply();

                }
                else
                {
                    SharedPreferences sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);
                    int no_of_days = sharedPreferences.getInt("days",0);

                    Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM task WHERE taskName = '"+taskList.get(position)+"'",null);
                    int dueHourIndex = c.getColumnIndex("dueHour");
                    int dueMinutesIndex = c.getColumnIndex("dueMinutes");
                    int dueDateIndex = c.getColumnIndex("dueDate");
                    int dueMonthIndex = c.getColumnIndex("dueMonth");
                    int dueYearIndex = c.getColumnIndex("dueYear");

                    c.moveToFirst();

                    Calendar due_calendar = Calendar.getInstance();
                    Calendar current_calendar = Calendar.getInstance();

                    while (!c.isAfterLast())
                    {
                        int dueHour = c.getInt(dueHourIndex);
                        int dueMinutes = c.getInt(dueMinutesIndex);
                        int dueDate = c.getInt(dueDateIndex);
                        int dueMonth = c.getInt(dueMonthIndex);
                        int dueYear = c.getInt(dueYearIndex);

                        due_calendar.set(Calendar.DAY_OF_MONTH,dueDate);
                        due_calendar.set(Calendar.MONTH,dueMonth-1);
                        due_calendar.set(Calendar.YEAR,dueYear);
                        due_calendar.set(Calendar.HOUR_OF_DAY,dueHour);
                        due_calendar.set(Calendar.MINUTE,dueMinutes);

                        c.moveToNext();
                    }

                    if(!current_calendar.after(due_calendar)) {
                        Toast.makeText(TaskActivity2.this,"Done before due Time",Toast.LENGTH_SHORT).show();
                        if(no_of_days <= 7)
                        {
                            int totaltaskdonetoday = sharedPreferences.getInt(no_of_days+"donetask",0);
                            sharedPreferences.edit().putInt(no_of_days+"donetask",++totaltaskdonetoday).apply();
                        }
                        else {
                            int totaltaskdonetoday = sharedPreferences.getInt("7donetask",0);
                            sharedPreferences.edit().putInt("7donetask",++totaltaskdonetoday).apply();
                        }

                        int overalldonetask = sharedPreferences.getInt("overalltaskdone",0);
                        overalldonetask++;
                        sharedPreferences.edit().putInt("overalltaskdone",overalldonetask).apply();
                    }
                    else
                        Toast.makeText(TaskActivity2.this, "Done Late", Toast.LENGTH_SHORT).show();

                    titleText.setBackgroundResource(R.drawable.titleheaderdselected);
                    subtitleText.setBackgroundResource(R.drawable.subtitleselected);
                    done.set(position,"Yes");
                    listView.getChildAt(position).setBackgroundResource(R.drawable.imageselected);
                    sqLiteDatabase.execSQL("UPDATE task set done = 'Yes' WHERE taskName = '"+taskList.get(position)+"'");
                    sqLiteDatabase.execSQL("UPDATE task set taskName = '"+taskList.get(position)+"\t(Marked as done)"+"' WHERE taskName = '"+taskList.get(position)+"'");
                    taskList.set(position,taskList.get(position)+"\t(Marked as done)");
                    Toast.makeText(TaskActivity2.this, "Marked as done", Toast.LENGTH_SHORT).show();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });


        //Delete the task using on long click
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                sqLiteDatabase.execSQL("DELETE FROM task WHERE taskName = '"+taskList.get(position)+"'");
                String name = taskList.get(position);
                done.remove(position);
                taskList.remove(position);
                dueList.remove(position);
                priority.remove(position);
                Toast.makeText(TaskActivity2.this, "Deleted "+name+" from your tasks", Toast.LENGTH_SHORT).show();
                arrayAdapter.notifyDataSetChanged();

                return true;
            }
        });

    }
}
