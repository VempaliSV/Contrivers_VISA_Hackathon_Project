package com.example.visa_project;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.EditText;
import com.example.visa_project.API.SendOTP;
import com.example.visa_project.API.VerifyOTP;
import java.sql.Timestamp;
import java.util.Random;

public class OTPActivity extends AppCompatActivity {
    String OTP = null;
    Timestamp generationTime = null;
    String mobile_number = null;
    public static LinearLayout llProgressBar;

    public void generateOTP(){
        generationTime = new Timestamp(System.currentTimeMillis());
        Random random = new Random();
        for(int i=0;i<6;i++){
            int digit = random.nextInt(10);
            OTP += String.valueOf(digit);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void verifyClick(View view){
        EditText otpEditText = (EditText) findViewById(R.id.otpEditText);
        String enteredOTP = otpEditText.getText().toString();
        if(enteredOTP.length() != 6){
            Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!enteredOTP.equals(OTP)){
            Toast.makeText(this, "Wrong OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        if((currentTime.getTime() - generationTime.getTime()) > 60000){
            // otp expired
            Toast.makeText(this, "OTP has expired. Resend OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        // activate user
        VerifyOTP verifyOTP = new VerifyOTP(OTPActivity.this);
        try {
            llProgressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            verifyOTP.execute("https://virtual-card-auth.herokuapp.com/otp", mobile_number);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendOTP(View view){
        generateOTP();
        SendOTP sendOTP = new SendOTP(OTPActivity.this);
        try {
            sendOTP.execute("https://virtual-card-auth.herokuapp.com/otp", mobile_number, OTP);
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        llProgressBar = findViewById(R.id.llProgressBar);
        mobile_number = getIntent().getStringExtra("mobile_number");
        sendOTP(null);
    }
}