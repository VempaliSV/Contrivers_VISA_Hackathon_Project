package com.example.visa_project.HomeActivityFragments.Wallets;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.visa_project.HomeActivityFragments.Wallets.WalletCardRecyclerViewAdapter;
import com.example.visa_project.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WalletCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public NetworkImageView walletImage;
    public TextView walletTitle;
    WalletCardRecyclerViewAdapter.OnWalletClickListener onWalletClickListener;
    public TextView walletAmount;

    public WalletCardViewHolder(@NonNull View itemView,
                                WalletCardRecyclerViewAdapter.OnWalletClickListener onWalletClickListener) {
        super(itemView);
        walletImage = itemView.findViewById(R.id.wallet_image);
        walletTitle = itemView.findViewById(R.id.wallet_title);
        walletAmount = itemView.findViewById(R.id.wallet_amount);

        this.onWalletClickListener = onWalletClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onWalletClickListener.onWalletClick(getAdapterPosition(), walletTitle.getText().toString());
    }
}
