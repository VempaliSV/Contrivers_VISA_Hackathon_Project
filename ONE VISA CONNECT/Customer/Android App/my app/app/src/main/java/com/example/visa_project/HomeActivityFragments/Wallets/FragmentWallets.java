package com.example.visa_project.HomeActivityFragments.Wallets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.visa_project.API.GetWalletAmount;
import com.example.visa_project.R;
import com.example.visa_project.HomeActivityFragments.GridItemDecoration;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentWallets extends Fragment {

    public static RecyclerView recyclerView = null;
    public static LinearLayout llProgressBar;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_wallets, container, false);

        //set up the wallet grid
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));

        llProgressBar = view.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);
        GetWalletAmount getWalletAmount = new GetWalletAmount(getContext(),view);
        getWalletAmount.execute("https://virtual-card-auth.herokuapp.com/virtual_card");

        int largePadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.wallet_grid_spacing_small);
        recyclerView.addItemDecoration(new GridItemDecoration(largePadding, smallPadding));

        return view;
    }
}
