package com.example.visa_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.hanks.passcodeview.PasscodeView;

import androidx.appcompat.app.AppCompatActivity;

public class PasswordActivity extends AppCompatActivity {
    PasscodeView passcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_activity);
        passcodeView= findViewById(R.id.passcodeView);
        passcodeView.setPasscodeLength(4).setLocalPasscode("1234").setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(PasswordActivity.this,"Wrong PIN", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String number) {
                Intent intent = new Intent();
                intent.putExtra("status", "true");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("status", "false");
        setResult(RESULT_OK, intent);
        finish();
    }
}