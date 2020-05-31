package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ArrayList<String> habitlist;
    ListView percentListView;
    ImageView star1,star2,star3,star4,star5;
    TextView taskbytask,habitbyhabit,habitpercent,taskpercent,habitincrdecr,taskincrdecr,special;
    SharedPreferences sharedPreferences;
    MyAdapter myAdapter;
    boolean noHabits,noTasks;

    SQLiteDatabase sqLiteDatabase;
    Cursor c;

    public Bitmap setImageFromByteArray(byte[] byImage){
        ByteArrayInputStream in = new ByteArrayInputStream(byImage);
        return BitmapFactory.decodeStream(in);
    }

    public byte[] getByteArrayFromImage(Bitmap image){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


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

            return view;
        }
    }


    public void editUserProfile(View view)
    {
        finish();
        Intent intent = new Intent(this,editProfile.class);
        startActivity(intent);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameTxt = findViewById(R.id.usernameTxt);
        ImageView userPhoto = findViewById(R.id.userphoto);
        special = findViewById(R.id.special);

        noHabits = false;
        noTasks = false;

        try {
            sharedPreferences = getSharedPreferences("com.example.todolist", MODE_PRIVATE);
            String myImageUri = sharedPreferences.getString("imageUri", null);
            userPhoto.setImageURI(Uri.parse(myImageUri));
            //userPhoto.invalidate();
        } catch (Exception e)
        {
            userPhoto.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
        finally {
            usernameTxt.setText(sharedPreferences.getString("username", "Username"));
            percentListView = findViewById(R.id.listviewreport);
            habitlist = new ArrayList<>();

            sqLiteDatabase = this.openOrCreateDatabase("habits",MODE_PRIVATE,null);
            c = sqLiteDatabase.rawQuery("SELECT * FROM habits",null);

            star1 = findViewById(R.id.firstStar);
            star2 = findViewById(R.id.secondStar);
            star3 = findViewById(R.id.thirdStar);
            star4 = findViewById(R.id.fourthStar);
            star5 = findViewById(R.id.fifthStar);

            taskbytask = findViewById(R.id.taskbytask);
            habitbyhabit = findViewById(R.id.habitbyhabit);
            taskincrdecr = findViewById(R.id.taskincrdecr);
            habitincrdecr = findViewById(R.id.habitincrdecr);
            habitpercent = findViewById(R.id.habitpercent);
            taskpercent = findViewById(R.id.taskpercent);

            float percentage;
            int habit_no = 0;

            int totaltasksoverall,totaltaskdoneoverall;
            totaltaskdoneoverall = sharedPreferences.getInt("overalltaskdone",0);
            totaltasksoverall = sharedPreferences.getInt("overalltask",0);

            int nameIndex  = c.getColumnIndex("name");
            int doneTimeIndex = c.getColumnIndex("doneTime");
            int totalTimeIndex = c.getColumnIndex("totalTime");

            c.moveToFirst();

            while (!c.isAfterLast())
            {
                habit_no++;
                String habitItem;
                habitItem = " " + c.getString(nameIndex) + " - ";
                if (c.getInt(totalTimeIndex) == 0)
                {
                    percentage = 0;
                }
                else
                    percentage = (c.getInt(doneTimeIndex)*100F / c.getInt(totalTimeIndex));
                habitItem += percentage + " %";
                habitlist.add(habitItem);
                c.moveToNext();
            }

            myAdapter = new MyAdapter(this,R.layout.custom_listview,habitlist) ;
            percentListView.setAdapter(myAdapter);
            setListViewHeightBasedOnChildren(percentListView);

            TextView textView = findViewById(R.id.habitindicator);
            if(habit_no == 0)
            {
                textView = findViewById(R.id.habitindicator);
                textView.setVisibility(View.VISIBLE);
            }
            else {
                textView.setVisibility(View.INVISIBLE);
            }

            int donehabits = sharedPreferences.getInt("overalldonehabits",0);
            int totalhabits = sharedPreferences.getInt("overallhabits",0);

            String habitbyhabitTextSetter = donehabits + "/" + totalhabits + " habits";
            String taskbytaskTextSetter = totaltaskdoneoverall + "/" + totaltasksoverall + " tasks";
            habitbyhabit.setText(habitbyhabitTextSetter);
            taskbytask.setText(taskbytaskTextSetter);

            int past_week_total_habits = 0;
            int past_week_done_habits = 0;

            for(int i = 1; i < 8; i++)
            {
                past_week_done_habits += sharedPreferences.getInt(i+"donehabit",0);
                past_week_total_habits += sharedPreferences.getInt(i+"totalhabit",0);
            }

            float percentage_of_habits_in_last_seven_days = 0;

            if (past_week_total_habits > 0)
             percentage_of_habits_in_last_seven_days = past_week_done_habits * 100F/past_week_total_habits;

            if (past_week_total_habits > 0) {
                String habitPercentText = percentage_of_habits_in_last_seven_days + "%";
                habitpercent.setText(habitPercentText);
            }
            else {
                noHabits = true;
                if(sharedPreferences.getInt("days",0) >= 8)
                {
                    habitpercent.setText("No habits to do in last week");
                }
                else {
                    habitpercent.setText("No habits to show for now");
                }
            }

            int task_total_in_last_seven_days = 0;
            int task_done_in_last_seven_days = 0;

            for(int i = 1; i < 8; i++)
            {
                task_total_in_last_seven_days+=sharedPreferences.getInt(i+"totaltask",0);
                task_done_in_last_seven_days+=sharedPreferences.getInt(i+"donetask",0);
            }

            float percentage_of_task_in_last_seven_days = 0;

            if (task_total_in_last_seven_days > 0)
            {
                percentage_of_task_in_last_seven_days = task_done_in_last_seven_days * 100f / task_total_in_last_seven_days;
                String taskPercentText = percentage_of_task_in_last_seven_days + "%";
                taskpercent.setText(taskPercentText);
            }
            else {
                noTasks = true;
                if (sharedPreferences.getInt("days",0) >= 8) {
                    taskpercent.setText("No tasks in past week");
                }
                else {
                    taskpercent.setText("No task to show");
                }
            }

            //Setting the increment decrement textViews according to past seven days report
            float user_overall_habit_percentage = 0;
            if (totalhabits > 0) {
                user_overall_habit_percentage = donehabits * 100F / totalhabits;
            }
            float user_overall_task_percentage = totaltaskdoneoverall * 100F / totaltasksoverall;

            float variation_in_habit_from_past_week =  percentage_of_habits_in_last_seven_days - user_overall_habit_percentage;
            float variation_in_task_from_past_week =  percentage_of_task_in_last_seven_days - user_overall_task_percentage;
            if(!noHabits)   //There are some habits to calculate variation on
            {
                if(variation_in_habit_from_past_week >= 0)
                {
                    String habitincrdecrSetter = "+" +  variation_in_habit_from_past_week + "% from overall";
                    habitincrdecr.setText(habitincrdecrSetter);
                    habitincrdecr.setTextColor(getResources().getColor(R.color.improve));
                }
                else
                {
                    String habitincrdecrSetter = variation_in_habit_from_past_week + "% from overall";
                    habitincrdecr.setText(habitincrdecrSetter);
                    habitincrdecr.setTextColor(getResources().getColor(R.color.decrease));
                }
            }
            else
            {
                habitincrdecr.setText("");
            }

            if(!noTasks){
                //Setting text for taskincrdecr from past week
                if (variation_in_task_from_past_week >= 0)
                {
                    String taskincrdecrSetter = "+" + variation_in_task_from_past_week + "% from overall";
                    taskincrdecr.setText(taskincrdecrSetter);
                    taskincrdecr.setTextColor(getResources().getColor(R.color.improve));
                }
                else
                {
                    String taskincrdecrSetter = variation_in_task_from_past_week + "% from overall";
                    taskincrdecr.setText(taskincrdecrSetter);
                    taskincrdecr.setTextColor(getResources().getColor(R.color.decrease));
                }
            }
            else
            {
                taskincrdecr.setText("");
            }

            if (totaltasksoverall == 0 && totalhabits == 0){
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_black_24dp);
                star3.setImageResource(R.drawable.ic_star_black_24dp);
                star4.setImageResource(R.drawable.ic_star_black_24dp);
                star5.setImageResource(R.drawable.ic_star_black_24dp);
            }
            float overall_user_percentage = 0;
            if(totaltasksoverall > 0 || totalhabits > 0)
              overall_user_percentage = (donehabits + totaltaskdoneoverall)*100F/(float) (totalhabits+ totaltasksoverall);
            Toast.makeText(this,"Your aggregate - "+ (overall_user_percentage) + " %", Toast.LENGTH_SHORT).show();
            if(overall_user_percentage > 90)
            {
                special.setVisibility(View.VISIBLE);
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_black_24dp);
                star3.setImageResource(R.drawable.ic_star_black_24dp);
                star4.setImageResource(R.drawable.ic_star_black_24dp);
                star5.setImageResource(R.drawable.ic_star_black_24dp);
            }
            else if(overall_user_percentage > 60)
            {
                special.setVisibility(View.INVISIBLE);
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_black_24dp);
                star3.setImageResource(R.drawable.ic_star_black_24dp);
                star4.setImageResource(R.drawable.ic_star_black_24dp);
                star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            else if(overall_user_percentage > 40)
            {
                special.setVisibility(View.INVISIBLE);
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_black_24dp);
                star3.setImageResource(R.drawable.ic_star_black_24dp);
                star4.setImageResource(R.drawable.ic_star_border_black_24dp);
                star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            else if (overall_user_percentage > 20)
            {
                special.setVisibility(View.INVISIBLE);
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_black_24dp);
                star3.setImageResource(R.drawable.ic_star_border_black_24dp);
                star4.setImageResource(R.drawable.ic_star_border_black_24dp);
                star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
            else if (overall_user_percentage >= 0)
            {
                special.setVisibility(View.INVISIBLE);
                star1.setImageResource(R.drawable.ic_star_black_24dp);
                star2.setImageResource(R.drawable.ic_star_border_black_24dp);
                star3.setImageResource(R.drawable.ic_star_border_black_24dp);
                star4.setImageResource(R.drawable.ic_star_border_black_24dp);
                star5.setImageResource(R.drawable.ic_star_border_black_24dp);
            }

        } //End of finally
    } //End of onCreate
} // End of class ProfileActivity
