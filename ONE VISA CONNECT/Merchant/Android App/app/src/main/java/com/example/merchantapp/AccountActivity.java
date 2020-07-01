package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;

        import android.annotation.SuppressLint;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.widget.TextView;

        import static com.example.merchantapp.HomeActivity.session;

public class AccountActivity extends AppCompatActivity {

    public void backButton(View view){
        this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        TextView profileHeading = (TextView) findViewById(R.id.profileHeading);
        TextView countrySubHeading = (TextView) findViewById(R.id.countrySubHeading);
        TextView full_name = (TextView) findViewById(R.id.fullNameContentTextView);
        TextView email = (TextView) findViewById(R.id.emailContentTextView);
        TextView mobile_number = (TextView) findViewById(R.id.mobileContentTextView);

        profileHeading.setText("Hi " + session.getUsername());
        countrySubHeading.setText(session.getCountry());
        full_name.setText(session.getUsername());
        email.setText(session.getEmail());
        mobile_number.setText(session.getMobileNumber());
    }
}