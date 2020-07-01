package com.example.visa_project;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class AmountActivity extends AppCompatActivity {
    EditText amountEditText;

    public void proceedClick(View view){
        String amountText = amountEditText.getText().toString();
        if(amountText.length() == 0){
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double amount = Double.parseDouble(amountText);
        if(amount < 1.00){
            Toast.makeText(this, "Minimum amount: 1 unit", Toast.LENGTH_SHORT).show();
            return;
        }
        if(amount > 5000.00){
            Toast.makeText(this, "Maximum amount: 5000 unit", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this,PaymentMethodActivity.class);
        intent.putExtra("amount", amountText);
        intent.putExtra("wallet_name", getIntent().getStringExtra("wallet_name"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amount_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        amountEditText = findViewById(R.id.amountEditText);
    }
}