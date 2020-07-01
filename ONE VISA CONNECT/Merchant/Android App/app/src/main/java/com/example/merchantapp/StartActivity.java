package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.merchantapp.utils.ISOCodes;

public class StartActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;
    Animation iconAnim,sloganAnim;
    ImageView icon;
    TextView slogan;
    public static ISOCodes isoCodes = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        isoCodes = new ISOCodes();

        //Animations
        iconAnim = AnimationUtils.loadAnimation(this,R.anim.app_icon_animation);
        sloganAnim = AnimationUtils.loadAnimation(this,R.anim.slogan_animation);

        icon = findViewById(R.id.visa_icon);
        slogan = findViewById(R.id.visa_slogan);

        icon.setAnimation(iconAnim);
        slogan.setAnimation(sloganAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent newIntent = new Intent(StartActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(icon,"icon");
                pairs[1] = new Pair<View,String>(slogan,"slogan");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartActivity.this,pairs);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newIntent,options.toBundle());
                finish();
            }
        },SPLASH_SCREEN);
    }

}