package com.example.visa_project.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A payment entry in the list of named history.
 */
public class PaymentEntry {
    private static final String TAG = PaymentEntry.class.getSimpleName();

    public final String wallet_name;
    public final String transaction_id;
    public final String amount;
    public final String transaction_time;
    public final String merchant_name;
    public final String status;

    public PaymentEntry(
            String walletName, String orderNumber, String amountPaid, String dateTime, String merchantOperator
            ,String orderResponse) {
        this.wallet_name = walletName;
        this.transaction_id = orderNumber;
        this.amount = amountPaid;
        this.transaction_time = dateTime;
        this.merchant_name = merchantOperator;
        this.status = orderResponse;
    }

    /**
     * Loads a JSON string and converts it into a list of PaymentEntry objects
     */
    public static List<PaymentEntry> initPaymentEntryList(String jsonWalletsString) {
        Gson gson = new Gson();
        Type paymentListType = new TypeToken<ArrayList<PaymentEntry>>() {
        }.getType();
        return gson.fromJson(jsonWalletsString, paymentListType);
    }
}