package com.example.visa_project.HomeActivityFragments.History;
import android.view.View;
import android.widget.TextView;

import com.example.visa_project.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentCardViewHolder extends RecyclerView.ViewHolder{

    public TextView orderNumber;
    public TextView walletName;
    public TextView amountPaid;
    public TextView dateTime;
    public TextView orderResponse;
    public TextView merchantOperator;

    public PaymentCardViewHolder(@NonNull View itemView) {
        super(itemView);
        orderNumber = itemView.findViewById(R.id.orderNumber);
        walletName = itemView.findViewById(R.id.walletName);
        amountPaid = itemView.findViewById(R.id.amountPaid);
        dateTime = itemView.findViewById(R.id.dateTime);
        merchantOperator = itemView.findViewById(R.id.merchantOperator);
        orderResponse = itemView.findViewById(R.id.orderResponse);
    }
}
