package com.example.visa_project;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.visa_project.API.GetPan;

public class GenerateQRActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ImageView qrImageView;
    public static int smallerDimension;
    public static LinearLayout llProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generate_qr_activity);
        llProgressBar = findViewById(R.id.llProgressBar);
        qrImageView = findViewById(R.id.qrImageView);

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        smallerDimension = Math.min(width, height);

        String wallet_name = getIntent().getStringExtra("wallet_name");
        String amount = getIntent().getStringExtra("amount");

        llProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        GetPan getPan = new GetPan(GenerateQRActivity.this);
        getPan.execute("https://virtual-card-auth.herokuapp.com/visa_net/payment", wallet_name, amount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}