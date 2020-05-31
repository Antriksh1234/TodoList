package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactActivity extends AppCompatActivity {

    TextView linkedInprofile,mailId,emailText,LinkedInText;
    ImageView backgroundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.contacttoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("    Contact us");
        getSupportActionBar().setLogo(R.drawable.ic_share_black_24dp);

        linkedInprofile = findViewById(R.id.profileLinkedIn);
        mailId = findViewById(R.id.email);
        emailText = findViewById(R.id.mailus);
        LinkedInText = findViewById(R.id.linkedIn);

        backgroundView = findViewById(R.id.bgimagecontact);

        backgroundView.animate().alpha(0.7F).setDuration(900);

        TranslateAnimation translation = new TranslateAnimation(0,0,-2500,0);
        translation.setFillAfter(true);
        translation.setStartOffset(900);
        translation.setDuration(900);

        linkedInprofile.startAnimation(translation);
        mailId.startAnimation(translation);
        emailText.startAnimation(translation);
        LinkedInText.startAnimation(translation);

    }
}
