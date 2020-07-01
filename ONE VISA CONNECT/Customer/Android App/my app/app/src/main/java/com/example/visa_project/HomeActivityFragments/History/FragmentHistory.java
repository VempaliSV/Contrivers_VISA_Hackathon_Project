package com.example.visa_project.HomeActivityFragments.History;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.visa_project.API.RequestHistory;
import com.example.visa_project.R;
import com.example.visa_project.HomeActivityFragments.GridItemDecoration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentHistory extends Fragment {
    public static RecyclerView recyclerView;
    public static LinearLayout llProgressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history,container,false);

        //set up the wallet grid
        recyclerView = view.findViewById(R.id.history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));

        llProgressBar = view.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        // get history from backend
        RequestHistory requestHistory = new RequestHistory(getContext());
        requestHistory.execute("Enter server port" + "/payment/history");

        int largePadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        return view;
    }
}
