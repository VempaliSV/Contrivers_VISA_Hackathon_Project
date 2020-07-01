package com.example.merchantapp.HistoryRecycleViewHolder;

import android.view.View;
import android.widget.TextView;

import com.example.merchantapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentCardViewHolder extends RecyclerView.ViewHolder{

    /*
    public final String customer_wallet_name;
    public final String transaction_time;
    public final String status;
    public final String transaction_id;
    public final String customer_mobile_number;
     */

    public TextView transactionId;
    public TextView walletName;
    public TextView amountReceived;
    public TextView transactionTime;
    public TextView orderResponse;
    public TextView customerMobile;

    public PaymentCardViewHolder(@NonNull View itemView) {
        super(itemView);
        transactionId = itemView.findViewById(R.id.transactionId);
        walletName = itemView.findViewById(R.id.walletName);
        amountReceived = itemView.findViewById(R.id.amountReceived);
        transactionTime = itemView.findViewById(R.id.transactionTime);
        customerMobile = itemView.findViewById(R.id.customerMobile);
        orderResponse = itemView.findViewById(R.id.orderResponse);
    }
}
