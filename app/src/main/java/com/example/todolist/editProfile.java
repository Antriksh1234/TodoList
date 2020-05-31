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


public class editProfile extends AppCompatActivity {

    static final int PICK_IMAGE_REQUEST = 1;
    SharedPreferences sharedPreferences,sp;
    EditText editText;
    ImageView imageView;

    Uri imageUri;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

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

            imageView.setImageURI(imageUri);
            //imageView.invalidate();
        }
    }

    public void edit(View view)
    {
        sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);;

        if(editText.getText().length() > 0)
        {
            if (imageUri!=null)
                sharedPreferences.edit().putString("imageUri",imageUri.toString()).apply();

            sharedPreferences.edit().putString("username",editText.getText().toString()).apply();
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(this,ProfileActivity.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);

        boolean firstTime  = sharedPreferences.getBoolean("editfirst",true);
        if (firstTime) {
            AlertDialog builder = new AlertDialog.Builder(this)
                    .setTitle("Instructions")
                    .setMessage("1.Tap on the image to select from the gallery\n\n2. Long press on the image to delete your profile pic")
                    .setIcon(android.R.drawable.ic_menu_help)
                    .setPositiveButton("Ok", null)
                    .show();
            sharedPreferences.edit().putBoolean("editfirst",false).apply();
        }

        editText = findViewById(R.id.editText);
        editText.setText(sharedPreferences.getString("username","username"));
        imageView = findViewById(R.id.usereditphoto);

        imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        sharedPreferences = getSharedPreferences("com.example.todolist",MODE_PRIVATE);

        String uri = sharedPreferences.getString("imageUri",null);
        if(uri != null) {
            imageUri = Uri.parse(uri);
            imageView.setImageURI(imageUri);
        }
        else {
            imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (imageUri!=null) {
                    sharedPreferences.edit().putString("imageUri", null).apply();
                    imageUri = null;
                    Toast.makeText(editProfile.this, "Deleted profile pic", Toast.LENGTH_SHORT).show();
                    imageView.invalidate();
                    imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                }
                return true;
            }
        });

    }
}
