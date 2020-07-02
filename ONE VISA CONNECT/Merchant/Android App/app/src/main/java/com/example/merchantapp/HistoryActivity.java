package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import com.example.merchantapp.API.RequestHistory;
import com.example.merchantapp.HistoryRecycleViewHolder.PaymentGridItemDecoration;

public class HistoryActivity extends AppCompatActivity {
    public static RecyclerView recyclerView = null;
    public static LinearLayout llProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //set up the wallet grid
        recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        llProgressBar = findViewById(R.id.llProgressBar);

        llProgressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        RequestHistory requestHistory = new RequestHistory(HistoryActivity.this);
        requestHistory.execute("Enter server port" + "/transaction/history");

        int largePadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing_small);
        recyclerView.addItemDecoration(new PaymentGridItemDecoration(largePadding, smallPadding));
    }
}