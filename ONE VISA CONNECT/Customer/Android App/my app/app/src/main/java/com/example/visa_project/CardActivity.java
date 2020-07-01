package com.example.visa_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.example.visa_project.SessionManager.SessionManager;

public class CardActivity extends AppCompatActivity {
    Toolbar toolbar;
    SessionManager session;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void payClick(View view){
        Intent intent = new Intent(this, AmountActivity.class);
        intent.putExtra("wallet_name", getIntent().getStringExtra("wallet_name"));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_activity);

        session = new SessionManager(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Wallet");
        setSupportActionBar(toolbar);
    }
}