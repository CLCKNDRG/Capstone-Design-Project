package com.example.videocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

public class RegistrationActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //컴포넌트 지정
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndNextBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);
        //국가 코드 버튼 지정
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText((phoneText));

        // 버튼 클릭
        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueAndNextBtn.getText().equals("확인") || checker.equals("비밀번호 전송됨")) {

                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    //Firebase를 활용하여 번호 확인
                    if(!phoneNumber.equals("")) {
                        loadingBar.setTitle("번호 사용 가능 여부 확인");
                        loadingBar.setMessage("확인 중입니다, 잠시만 기다려 주세요!");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                    }else {
                        Toast.makeText(RegistrationActivity.this, "유효하지 않은 번호입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}