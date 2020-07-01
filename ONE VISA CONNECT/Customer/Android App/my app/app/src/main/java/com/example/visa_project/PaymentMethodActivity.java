package com.example.visa_project;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PaymentMethodActivity extends AppCompatActivity {
    String payload;
    String amount;
    String wallet_name;

    public void posClick(View view){
        Intent intent = new Intent(this, PasswordActivity.class);
        startActivityForResult(intent, 2);
    }

    public void qrClick(View view){
        Intent intent = new Intent(this, ScanQRActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("wallet_name", wallet_name);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == 1){
                payload = data.getStringExtra("payload");
                Intent intent = new Intent(this, PasswordActivity.class);
                startActivityForResult(intent, 3);
            } else if(requestCode == 2){
                String status = data.getStringExtra("status");
                if(status.equals("true")){
                    // pin is verified
                    // generate qr code
                    Intent intent = new Intent(this, GenerateQRActivity.class);
                    intent.putExtra("amount", amount);
                    intent.putExtra("wallet_name", wallet_name);
                    startActivity(intent);
                }else if(status.equals("false")){
                    payload = null;
                }
            } else if(requestCode == 3){
                String status = data.getStringExtra("status");
                if(status.equals("true")){
                    // payload is done and pin is verified
                    // PAY
                    Intent intent = new Intent(this, PaymentResultActivity.class);
                    intent.putExtra("payload", payload);
                    startActivity(intent);
                }else if(status.equals("false")){
                    payload = null;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        payload = null;
        amount = getIntent().getStringExtra("amount");
        wallet_name = getIntent().getStringExtra("wallet_name");
    }
}