package com.example.visa_project.HomeActivityFragments.Wallets;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.visa_project.R;
import com.example.visa_project.network.ImageRequester;
import com.example.visa_project.network.WalletEntry;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter used to show a simple grid of wallets.
 */
public class WalletCardRecyclerViewAdapter extends RecyclerView.Adapter<WalletCardViewHolder> {

    private List<WalletEntry> walletList;
    private ImageRequester imageRequester;
    private OnWalletClickListener onWalletClickListener;

    public WalletCardRecyclerViewAdapter(List<WalletEntry> walletList, OnWalletClickListener onWalletClickListener) {
        this.walletList = walletList;
        imageRequester = ImageRequester.getInstance();
        this.onWalletClickListener = onWalletClickListener;
    }

    @NonNull
    @Override
    public WalletCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_card, parent, false);
        return new WalletCardViewHolder(layoutView,onWalletClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletCardViewHolder holder, int position) {
        if (walletList != null && position < walletList.size()) {
            WalletEntry wallet = walletList.get(position);
            holder.walletTitle.setText(wallet.walletName);
            holder.walletAmount.setText((wallet.amount));
            imageRequester.setImageFromUrl(holder.walletImage, wallet.url);
        }
    }

    @Override
    public int getItemCount() {
        return walletList.size();
    }

    public interface OnWalletClickListener{
        void onWalletClick(int position, String wallet_name);
    }
}
