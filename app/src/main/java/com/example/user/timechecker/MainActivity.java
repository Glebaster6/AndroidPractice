package com.example.user.timechecker;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private EditText secondName;
    private EditText firstName;
    private EditText fatherName;
    private Button confirmButton;
    private ImageView photo;
    private Gson gson;
    private SharedPreferences sharedPreferences;

    public static final String KEY_USER = "user";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_DATE = "date";
    public static final int CAMERA_REQUEST = 1;
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        sharedPreferences = getPreferences(MODE_PRIVATE);
        secondName = (EditText) findViewById(R.id.second_name);
        firstName = (EditText) findViewById(R.id.first_name);
        fatherName = (EditText) findViewById(R.id.father_name);
        confirmButton = (Button) findViewById(R.id.confirm_button);
        photo = (ImageView) findViewById(R.id.photo);

        if(new Date().after(new Date(getTime(KEY_DATE)))) {
            if(getTime(KEY_DATE) != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Важное сообщение!")
                        .setMessage("Ваше время истекло ")
                        .setCancelable(false)
                        .setNegativeButton("Лады",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
            secondName.setText("");
            firstName.setText("");
            fatherName.setText("");
            photo.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            User user = getUser(KEY_USER);
            secondName.setText(user.getSecondName());
            firstName.setText(user.getFirstName());
            fatherName.setText(user.getFatherName());
            getPhoto(KEY_PHOTO);
        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermissionForCamera()) {
                    checkPermissionForCamera();
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                }
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(secondName.getText().toString(), firstName.getText().toString(), fatherName.getText().toString());
                putUser(KEY_USER, user);
                BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
                Bitmap bm = drawable.getBitmap();
                putPhoto(KEY_PHOTO, bm);
                Date date = new Date();
                putTime(KEY_DATE, date.getTime() + (30 * 1000));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
            photo.setImageBitmap(bitmap);
        }
    }

    public boolean checkPermissionForCamera() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForCamera() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                    Manifest.permission.CAMERA
            }, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void putPhoto(String key, Bitmap bm) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encoded = Base64.encodeToString(b, Base64.DEFAULT);
        editor.putString(key, encoded);
        editor.apply();
    }

    private void getPhoto(String key) {
        String encoded = sharedPreferences.getString(key, "");
        if(encoded.equals("")) {
            photo.setImageResource(R.mipmap.ic_flag);
        }
        else {
            byte[] b = Base64.decode(encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            photo.setImageBitmap(bitmap);
        }
    }

    private void putUser(String key, User user) {
        String json = gson.toJson(user);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, json);
        editor.apply();
    }

    private User getUser(String key) {
        String json = sharedPreferences.getString(key, "{}");
        if(json.equals("{}")) {
            return new User("", "", "");
        }

        return gson.fromJson(json, User.class);
    }

    private void putTime(String key, long i) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, i);
        editor.apply();
    }

    private long getTime(String key){
        return sharedPreferences.getLong(key, 0);
    }
}