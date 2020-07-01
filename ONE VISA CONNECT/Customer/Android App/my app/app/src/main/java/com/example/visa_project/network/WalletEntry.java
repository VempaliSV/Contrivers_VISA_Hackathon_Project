package com.example.visa_project.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A wallet entry in the list of wallets.
 */
public class WalletEntry {
    private static final String TAG = WalletEntry.class.getSimpleName();

    public final String walletName;
    public final String url;
    public final String amount;

    public WalletEntry(
            String walletName, String url, String amount) {
        this.walletName = walletName;
        this.url = url;
        this.amount = amount;
    }

    /**
     * Accepts a JSON string as a parameter and converts it into a list of WalletEntry objects
     */
    public static List<WalletEntry> initWalletEntryList(String jsonWalletsString) {
        Gson gson = new Gson();
        Type walletListType = new TypeToken<ArrayList<WalletEntry>>() {
        }.getType();
        return gson.fromJson(jsonWalletsString, walletListType);
    }
}