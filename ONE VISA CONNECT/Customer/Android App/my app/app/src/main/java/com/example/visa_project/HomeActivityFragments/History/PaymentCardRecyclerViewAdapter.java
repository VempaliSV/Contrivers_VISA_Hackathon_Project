package com.example.visa_project.HomeActivityFragments.History;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.visa_project.R;
import com.example.visa_project.network.PaymentEntry;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
            holder.walletName.setText(("Done by " + payment.wallet_name));
            holder.amountPaid.setText(("Amount : " + payment.amount));
            holder.orderNumber.setText(("Order Number : " + payment.transaction_id));
            holder.dateTime.setText(payment.transaction_time);
            holder.merchantOperator.setText(("Paid to : " + payment.merchant_name));
            holder.orderResponse.setText(("Your Payment is "+ payment.status));
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }
}
