package com.example.todolist;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AccountActivity extends AppCompatActivity {

    private EditText editText;
    private boolean imgSelected;
    static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri = null;

    public void openGallery(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
       {
           startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
       }
       else {
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PICK_IMAGE_REQUEST);
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            //Permission denied but not forever hence shouldShowRequestPermissionRationale() returns true, this method returns false only when permission is accepted or if 'never ask again' is done by the user as in that case too permission is not asked
            //Toast.makeText(this, "You denied permission", Toast.LENGTH_SHORT).show();
            AlertDialog builder= new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_menu_gallery)
                    .setTitle("Why we need this?")
                    .setMessage("We need to access media to get the image from the gallery.")
                    .setNeutralButton("Ok",null)
                    .show();
        }
        else {
            if(requestCode == PICK_IMAGE_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
                //startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
            else
            {
                //Permission made by to be never asked again, could be only fixed via settings
                AlertDialog builder = new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("You denied permission forever")
                        .setMessage("If you later want to add your profile pic, you may go and enable the permission in the settings app")
                        .setPositiveButton("Ok",null)
                        .show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageUri = data.getData();

            this.grantUriPermission(this.getPackageName(), imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            this.getContentResolver().takePersistableUriPermission(imageUri, takeFlags);

            ImageView imageView = findViewById(R.id.imageView);
            imageView.setBackground(null);
            imageView.invalidate();
            imageView.setImageURI(imageUri);
            imgSelected = true;
            //imageView.invalidate();
        }
    }

    public void signup(View view)
    {
       if(editText.getText().length() > 0)
       {
           SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.todolist",MODE_PRIVATE);
           sharedPreferences.edit().putString("username",editText.getText().toString()).apply();
           if (imgSelected)
               sharedPreferences.edit().putString("imageUri",String.valueOf(imageUri)).apply();
           else
               sharedPreferences.edit().putString("imageUri",null).apply();
           Toast.makeText(this, "Username added", Toast.LENGTH_SHORT).show();
           finish();
           Intent intent = new Intent(AccountActivity.this,MainActivity.class);
           startActivity(intent);
           sharedPreferences.edit().putInt("usedno",1).apply();

       }
       else
       {
           Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
       }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        editText = findViewById(R.id.username);
        imgSelected = false;

        Toast.makeText(this, "Tap on the image to select your Profile pic from the gallery", Toast.LENGTH_LONG).show();

    }
}
