package com.example.visa_project.API;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.visa_project.CardActivity;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;

import static com.example.visa_project.API.GetWalletAmount.llWalletProgressBar;
import static com.example.visa_project.HomeActivity.session;

/*
API request to check if the user has the virtual card synced to wallets or not (GET):
    Request: access_token
    Response:
        confirmation on the request
*/

public class CheckCard extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;
    String wallet_name = null;

    public CheckCard(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;

        // wallet selected
        wallet_name = strings[1];
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

            // flag if we got the response or not
            flag = "ok";
        } catch (Exception e) {
            // couldn't make a connection
            e.printStackTrace();
        }
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String flag) {
        super.onPostExecute(flag);
        Activity wallets = (Activity)context;

        if(flag == null){
            llWalletProgressBar.setVisibility(View.GONE);
            wallets.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 200){
                    llWalletProgressBar.setVisibility(View.GONE);
                    wallets.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    // if we got positive response then show virtual card
                    Intent intent = new Intent(context, CardActivity.class);
                    intent.putExtra("wallet_name", wallet_name);
                    context.startActivity(intent);
                }else if(statusCode == 404){
                    // card doesn't exists....make a post request to generate card
                    GenerateCard generateCard = new GenerateCard(context);
                    generateCard.execute("Enter server port" + "/virtual_card", wallet_name);
                }else{
                    llWalletProgressBar.setVisibility(View.GONE);
                    wallets.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(context, responseJSON.toString(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                llWalletProgressBar.setVisibility(View.GONE);
                wallets.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                // couldn't parse response into JSON
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
