package com.example.merchantapp;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.merchantapp.API.RequestLogout;
import com.example.merchantapp.SessionManager.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;
    public static SessionManager session;
    public static LinearLayout llProgressBar;

    public void logout(){
        String access_token = session.getAccessToken();

        if(access_token == null){
            Toast.makeText(this, "Token access error....", Toast.LENGTH_SHORT).show();
            return;
        }

        llProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        RequestLogout requestLogout = new RequestLogout(HomeActivity.this);
        requestLogout.execute("https://merchant-api-v1.herokuapp.com/logout", access_token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        llProgressBar = findViewById(R.id.llProgressBar);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        session = new SessionManager(HomeActivity.this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextNavigationDrawer = (TextView) headerView.findViewById(R.id.emailTextNavigationDrawer);
        emailTextNavigationDrawer.setText(session.getEmail());

        MaterialButton recevieButton = findViewById(R.id.receiveButton);

        recevieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AmountActivity.class);
                startActivity(intent);
            }
        });
        MaterialButton qrButton = findViewById(R.id.qrButton);

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, GenerateQRActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.help:
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;
            case R.id.account:
                Intent intentAccount = new Intent(this, AccountActivity.class);
                startActivity(intentAccount);
                break;
            case R.id.history:
                Intent intentHistory = new Intent(this, HistoryActivity.class);
                startActivity(intentHistory);
                break;
            case R.id.logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else{
            super.onBackPressed();
        }
    }
}