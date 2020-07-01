package com.example.visa_project;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.visa_project.API.RequestLogout;
import com.example.visa_project.HomeActivityFragments.History.FragmentHistory;
import com.example.visa_project.HomeActivityFragments.FragmentHome;
import com.example.visa_project.HomeActivityFragments.Wallets.FragmentWallets;
import com.example.visa_project.SessionManager.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    NavigationView navigationView;
    BottomNavigationView bottomNavigationView;
    public static LinearLayout llProgressBar;

    public static SessionManager session;

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
        requestLogout.execute("https://virtual-card-auth.herokuapp.com/logout", access_token);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        llProgressBar = findViewById(R.id.llProgressBar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new FragmentHome())
                    .commit();

            bottomNavigationView.setSelectedItemId(R.id.home);
        }
        session = new SessionManager(HomeActivity.this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextNavigationDrawer = headerView.findViewById(R.id.emailTextNavigationDrawer);
        emailTextNavigationDrawer.setText(session.getEmail());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()){
                        case R.id.home:
                            selectedFragment = new FragmentHome();
                            break;
                        case R.id.wallets:
                            selectedFragment = new FragmentWallets();
                            break;
                        case R.id.history:
                            selectedFragment = new FragmentHistory();
                            break;
                    }

                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,selectedFragment)
                            .commit();

                    return true;
                }
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                break;
            case R.id.help:
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;
            case R.id.account:
                Intent intentAccount = new Intent(this, UserProfileActivity.class);
                startActivity(intentAccount);
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