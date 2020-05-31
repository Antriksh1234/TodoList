package com.example.todolist;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HabitActivity extends AppCompatActivity {

    private ListView listView;
    private EditText HabitText;
    private Button addNew,cancel;
    private Button addButton;
    private TextView wooho;
    EditText habitText;
    AlertDialog alertDialog;
    private SQLiteDatabase sqLiteDatabase;
    static MyAdapter arrayAdapter;
    private static ArrayList<String> habits;
    private static ArrayList<String> habitStatus;

    private int doneTaskCount = 0;

    //Creating our own adapter for overriding getView() method to set the color of listView item as per the habit's status
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

    //Used to set the height of the ListView according to the no of elements so that there is no scrooling activty within the scrollview, it is not allowed as of May 1, 2020
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight()+50;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1))+50;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    //Method for adding a habit, opens up the  AlertDialog
    public void addAHabit(View view)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view1 = getLayoutInflater().inflate(R.layout.addhabit,null);

        alert.setView(view1);

        alertDialog = alert.create();

        habitText = (EditText) view1.findViewById(R.id.habitname);
        addNew = (Button) view1.findViewById(R.id.addOne);
        cancel = (Button) view1.findViewById(R.id.cancelhabit);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(habitText.getText().length() > 0)
                {
                    sqLiteDatabase.execSQL("INSERT INTO habits(name,done,doneTime,totalTime) VALUES ('"+habitText.getText().toString()+"','No',0,0)");
                    habits.add(" "+habitText.getText().toString());
                    habitStatus.add("No");
                    arrayAdapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(listView);
                    wooho.setVisibility(View.INVISIBLE);
                    alertDialog.dismiss();
                    Toast.makeText(HabitActivity.this, "Added", Toast.LENGTH_SHORT).show();

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
                else
                {
                    Toast.makeText(HabitActivity.this, "Please enter the name of habit!", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_habit);

        Toolbar toolbar = findViewById(R.id.habittoolbar);

        setSupportActionBar(toolbar);

       getSupportActionBar().setTitle("Make new Habits - TodoList");

       SharedPreferences sharedPreferences1 = this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);
       boolean firstTime = sharedPreferences1.getBoolean("HabitInstructions",true);
       if (firstTime)
       {
           AlertDialog builder = new AlertDialog.Builder(this)
                   .setTitle("Instructions")
                   .setMessage("1. To add a habit press the button 'add a habit' and add accordingly the habit you want. \n\n2. To mark a habit as done for today tap on the habit you want to mark. However if you accidently marked it as done you can again tap on it to unmark.\n\n3. Long press on any habit to delete it")
                   .setNeutralButton("Ok",null)
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

        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);
        wooho = findViewById(R.id.wooho);


        listView.setTranslationY(-2500);
        addButton.setTranslationY(-2500);
        listView.animate().translationYBy(2500).setDuration(700);
        addButton.animate().translationYBy(2500).setDuration(700);

        arrayAdapter = new MyAdapter(this,R.layout.custom_listview,habits);
        listView.setAdapter(arrayAdapter);
        setListViewHeightBasedOnChildren(listView);

        if(habits.size() == 0)
        {
            wooho.setText("Your habits appear above");
            wooho.setVisibility(View.VISIBLE);
        }
        else if (habits.size() > 0 && doneTaskCount == habits.size())
        {
            wooho.setText("Wooho, All habits performed today!");
            wooho.setVisibility(View.VISIBLE);
        }

        // OnItemClickListener for listView
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
                    Toast.makeText(HabitActivity.this, "Marked as done", Toast.LENGTH_SHORT).show();

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


                     if(habits.size() == 0)
                     {
                         wooho.setVisibility(View.VISIBLE);
                         wooho.setText("Your habits appear above");
                     }
                     else {
                         habits.size();
                         if (doneTaskCount == habits.size())
                         {
                             wooho.setText("Wooho, All habits performed today!");
                             wooho.setVisibility(View.VISIBLE);
                         }
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
                    wooho.setVisibility(View.INVISIBLE);
                    listView.getChildAt(position).setBackgroundResource(R.drawable.buttoncapsule);
                    sqLiteDatabase.execSQL("UPDATE habits set done = 'No' WHERE name = '"+habits.get(position).substring(1)+"'");
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(HabitActivity.this, "Marked as not done", Toast.LENGTH_SHORT).show();
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
              setListViewHeightBasedOnChildren(listView);

                if(habits.size() == 0)
                {
                    wooho.setText("Your habits appear above");
                    wooho.setVisibility(View.VISIBLE);
                }
                else if (habits.size() > 0 && doneTaskCount == habits.size())
                {
                    wooho.setText("Wooho, All habits performed today!");
                    wooho.setVisibility(View.VISIBLE);
                }
                Toast.makeText(HabitActivity.this, "Deleted "+nameOfHabit+" from your list", Toast.LENGTH_SHORT).show();
              return true;
            }
        });
    }

}
