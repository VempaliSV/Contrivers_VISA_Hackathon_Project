package com.example.visa_project.API;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.visa_project.HomeActivityFragments.Wallets.WalletCardRecyclerViewAdapter;
import com.example.visa_project.R;
import com.example.visa_project.network.WalletEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;

import static com.example.visa_project.HomeActivityFragments.Wallets.FragmentWallets.llProgressBar;
import static com.example.visa_project.HomeActivityFragments.Wallets.FragmentWallets.recyclerView;
import static com.example.visa_project.HomeActivity.session;

/*
API request to get wallet balance (GET):
    Request: access_token
    Response:
        List of all the wallets and their amounts (only 1 in our implementation)
*/

public class GetWalletAmount extends AsyncTask<String, Void, String> implements WalletCardRecyclerViewAdapter.OnWalletClickListener{

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;
    View view;
    public static LinearLayout llWalletProgressBar;

    public GetWalletAmount(Context context,View view) {
        this.context = context;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + session.getAccessToken());
            conn.setDoInput(true);

            // get the status code
            statusCode = conn.getResponseCode();

            InputStream inputStream;
            if(statusCode >= 200 && statusCode < 400){
                inputStream = conn.getInputStream();
            }else{
                inputStream = conn.getErrorStream();
            }

            // read the response
            int chr;
            response = "";
            while ((chr = inputStream.read()) != -1) {
                response += (char) chr;
            }
            inputStream.close();

            conn.disconnect();

            // flag to check if we got the response or not
            flag = "ok";
        } catch (Exception e) {
            // couldn't make a connection
            e.printStackTrace();
        }
        return flag;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(String flag) {
        super.onPostExecute(flag);
        llProgressBar.setVisibility(View.GONE);
        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {

                // get the response JSON
                JSONObject responseJSON = new JSONObject(response);
                String status = null;
                if(statusCode == 200){
                    // status 200 show balance
                    status = "Balance : " + responseJSON.getString("wallet_amount");
                }else if(statusCode == 404){
                    // card not found
                    status = "Tap to Sync";
                }else{
                    Toast.makeText(context, responseJSON.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // making the JSONArray for wallets (this array contains all the wallets' information in JSON format)
                JSONArray walletArray = new JSONArray();
                JSONObject wallet = new JSONObject();
                wallet.put("walletName", "Demo Wallet");
                wallet.put("url", "https://nuwallpaperhd.info/wp-content/uploads/2018/01/Awesome-Abstract-Background-Wallpapers.jpg"); // sample image
                wallet.put("amount", status);
                walletArray.put(wallet);

                // setting the UI to show wallets
                WalletCardRecyclerViewAdapter adapter = new WalletCardRecyclerViewAdapter(
                        WalletEntry.initWalletEntryList(walletArray.toString()),this);
                recyclerView.setAdapter(adapter);
            }catch (Exception e){
                // couldn't parse response into JSON
                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onWalletClick(int position, String wallet_name) {

        Activity wallets = (Activity)context;
        llWalletProgressBar = view.findViewById(R.id.llProgressBar);
        llWalletProgressBar.setVisibility(View.VISIBLE);

        wallets.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        CheckCard checkCard = new CheckCard(context);
        checkCard.execute("Enter server port" + "/virtual_card", wallet_name);
    }
}
