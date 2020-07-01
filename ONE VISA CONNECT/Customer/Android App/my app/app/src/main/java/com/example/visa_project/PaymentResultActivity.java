package com.example.visa_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.visa_project.API.RequestPayment;

public class PaymentResultActivity extends AppCompatActivity {
    public static LinearLayout llProgressBar;
    public static ImageView successImageView;
    public static ImageView failImageView;
    public static TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        successImageView = findViewById(R.id.successImageView);
        failImageView = findViewById(R.id.failImageView);
        statusTextView = findViewById(R.id.statusTextView);

        // add progress bar
        llProgressBar = findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        RequestPayment requestPayment = new RequestPayment(PaymentResultActivity.this);
        requestPayment.execute("https://virtual-card-auth.herokuapp.com/virtual_card/payment", getIntent().getStringExtra("payload"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}