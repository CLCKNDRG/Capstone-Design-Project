package com.example.videocalling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    //오브젝트 선언
    private Button saveBtn;
    private EditText userNameEdit, userBioEdit;
    private ImageView profileImageView;

    private static int GalleryPick = 1;
    private Uri ImageUri;
    private StorageReference userProfileImgRef;
    private String downloadUrl;
    private DatabaseReference userRef;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        userProfileImgRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        //컴포넌트 지정
        saveBtn = findViewById(R.id.save_settings_btn);
        userNameEdit = findViewById(R.id.usrname_settings);
        userBioEdit = findViewById(R.id.bio_settings);
        profileImageView = findViewById(R.id.settings_profile_image);
        progressDialog = new ProgressDialog(this);

        //프로필 이미지를 누르면 갤러리를 열도록 한다
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });

        //저장 버튼을 클릭할 경우
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }

        });

        retrieveUserInfo();
    }

    //갤러리에서 받아온 이미지를 프로필 화면에 삽입
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null) {
            ImageUri = data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }

    //유저의 정보를 저장
    private void saveUserData() {

        //이름과 상태메세지를 저장
        final String getUserName = userNameEdit.getText().toString();
        final String getUserStatus = userBioEdit.getText().toString();

        //프로필사진을 저장, 이름과 상태메세지의 입력 여부 또한 판단한다.
        if (ImageUri == null){
            //업로드된 이미지가 없는 경우
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //업로드된 이미지가 이미 존재하는 경우
                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image:")) {
                        saveInfoOnlyWithoutImage();
                    } else {
                        Toast.makeText(SettingsActivity.this, "프로필 사진이 없으면 신분을 확인하기 어려워요, 프로필 사진을 선택해 주세요!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else if (getUserName.equals("")) {
            //이름이 입력되지 않았을 경우
            Toast.makeText(this, "이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else if (getUserStatus.equals("")) {
            //상태메세지가 입력되지 않았을 경우
            Toast.makeText(this, "상태메세지를 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            //이미지와 이름, 상태메세지가 정상적으로 입력되었을 경우
            progressDialog.setTitle("계정 설정");
            progressDialog.setMessage("변경 중입니다. 잠시만 기다려주세요.");
            progressDialog.show();;

            //FireBase에 이미지 업로드 처리
            final StorageReference filePath
                    = userProfileImgRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final UploadTask uploadTask = filePath.putFile(ImageUri);
            //이미지 업로드를 시도한다
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        throw task.getException();
                    }
                    downloadUrl = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            //업로드가 성공하였을 경우
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        //이미지의 URL 값을 불러온다
                        downloadUrl = task.getResult().toString();

                        //유저의 데이터를 해시로 변환하여 저장한다
                        HashMap <String, Object> profileMap = new HashMap<>();
                        profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name", getUserName);
                        profileMap.put("status", getUserStatus);
                        profileMap.put("image", downloadUrl);

                        //FireBase로부터 업데이트된 프로필을 가져온다
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
                                    startActivity(intent);
                                    finish();
                                    progressDialog.dismiss();

                                    Toast.makeText(SettingsActivity.this, "프로필 설정이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    //이미지를 새로 저장하지 않고 이름과 상태메세지만 저장
    private void saveInfoOnlyWithoutImage() {
        //이름과 상태메세지를 저장
        final String getUserName = userNameEdit.getText().toString();
        final String getUserStatus = userBioEdit.getText().toString();

        //경우에 따른 조건분기
        if (getUserName.equals("")) {
            //이름이 입력되지 않았을 경우
            Toast.makeText(this, "이름을 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else if (getUserStatus.equals("")) {
            //상태메세지가 입력되지 않았을 경우
            Toast.makeText(this, "상태메세지를 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            //이미지와 이름, 상태메세지가 정상적으로 입력되었을 경우
            progressDialog.setTitle("설정 변경");
            progressDialog.setMessage("변경 중입니다. 잠시만 기다려주세요.");
            progressDialog.show();;

            //유저의 데이터를 해시로 변환하여 저장한다
            HashMap <String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name", getUserName);
            profileMap.put("status", getUserStatus);
            profileMap.put("image", downloadUrl);

            //FireBase로부터 업데이트된 프로필을 가져온다
            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Intent intent = new Intent(SettingsActivity.this, ContactsActivity.class);
                        startActivity(intent);
                        finish();
                        progressDialog.dismiss();

                        Toast.makeText(SettingsActivity.this, "프로필 설정이 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void retrieveUserInfo() {
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String imageDb = dataSnapshot.child("image").getValue().toString();
                            String nameDb = dataSnapshot.child("name").getValue().toString();
                            String statusDb = dataSnapshot.child("status").getValue().toString();

                            userNameEdit.setText(nameDb);
                            userBioEdit.setText(statusDb);
                            Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(profileImageView);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}