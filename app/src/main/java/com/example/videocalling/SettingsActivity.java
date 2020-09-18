package com.example.videocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    //오브젝트 선언
    private Button saveBtn;
    private EditText userNameEdit, userBioEdit;
    private ImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //컴포넌트 지정
        saveBtn = findViewById(R.id.save_settings_btn);
        userNameEdit = findViewById(R.id.usrname_settings);
        userBioEdit = findViewById(R.id.bio_settings);
        profileImageView = findViewById(R.id.settings_profile_image);

    }
}