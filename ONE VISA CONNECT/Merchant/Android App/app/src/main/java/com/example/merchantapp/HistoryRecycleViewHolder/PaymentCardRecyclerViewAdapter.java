package com.example.merchantapp.HistoryRecycleViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.merchantapp.Network.PaymentEntry;
import com.example.merchantapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter used to show a simple grid of payments in history fragment.
 */
public class PaymentCardRecyclerViewAdapter extends RecyclerView.Adapter<PaymentCardViewHolder> {

    private List<PaymentEntry> paymentList;

    public PaymentCardRecyclerViewAdapter(List<PaymentEntry> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_card, parent, false);
        return new PaymentCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentCardViewHolder holder, int position) {
        if (paymentList != null && position < paymentList.size()) {
            PaymentEntry payment = paymentList.get(position);
            holder.walletName.setText(("Done by VISA"));
            holder.amountReceived.setText(("Amount : " + payment.amount));
            holder.transactionId.setText(("Order Number : " + payment.transaction_id));
            String time = payment.transaction_time;
            String timeNew  = time.substring(0, 10) + " " + time.substring(11) + " GMT";
            holder.transactionTime.setText(timeNew);
            holder.customerMobile.setText(("Paid by : " + payment.customer_mobile_number));
            if(payment.status.equals("true")){
                holder.orderResponse.setText(("Payment Received"));
            }else{
                holder.orderResponse.setText(("Payment Declined"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }
}
