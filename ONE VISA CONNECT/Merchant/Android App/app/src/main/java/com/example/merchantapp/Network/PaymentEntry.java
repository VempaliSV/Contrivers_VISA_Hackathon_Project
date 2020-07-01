package com.example.merchantapp.Network;

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

    public final String amount;
    public final String customer_wallet_name;
    public final String transaction_time;
    public final String status;
    public final String transaction_id;
    public final String customer_mobile_number;
    public final String merchant_mobile_number;
    public final String merchant_name;




    public PaymentEntry(
            String amount, String customer_wallet_name, String transaction_id, String transaction_time, String merchant_mobile_number
            ,String status, String customer_mobile_number, String merchant_name) {
        this.amount = amount;
        this.customer_wallet_name = customer_wallet_name;
        this.transaction_id = transaction_id;
        this.transaction_time = transaction_time;
        this.merchant_mobile_number= merchant_mobile_number;
        this.status = status;
        this.customer_mobile_number = customer_mobile_number;
        this.merchant_name = merchant_name;

    }

    /**
     * Loads a raw JSON at R.raw.wallets and converts it into a list of ProductEntry objects
     */
    public static List<PaymentEntry> initPaymentEntryList(String jsonWalletsString) {
        Gson gson = new Gson();
        Type paymentListType = new TypeToken<ArrayList<PaymentEntry>>() {
        }.getType();
        return gson.fromJson(jsonWalletsString, paymentListType);
    }
}