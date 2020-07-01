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
import com.example.visa_project.HomeActivity;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import static com.example.visa_project.API.GetWalletAmount.llWalletProgressBar;

/*
API request to generate a virtual card for the user (POST):
    Request: access_token, mobile_number
    Response:
        confirmation on creation of wallet
*/

public class GenerateCard extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;
    String wallet_name = null;

    public GenerateCard(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;

        // selected wallet
        wallet_name = strings[1];
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + HomeActivity.session.getAccessToken());
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // body of the request
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("mobile_number", HomeActivity.session.getMobileNumber());

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(jsonParam.toString());
            outputStream.flush();
            outputStream.close();

            // get the response code
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

            flag = "ok";
        } catch (Exception e) {
            // couldn't make a connection
            e.printStackTrace();
        }

        // flag to check if we go the response or not
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPostExecute(String flag) {
        super.onPostExecute(flag);
        Activity wallets = (Activity)context;
        llWalletProgressBar.setVisibility(View.GONE);
        wallets.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {

                // get the response JSON
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 201){
                    // if card is generated then show the virtual card
                    Intent intent = new Intent(context, CardActivity.class);
                    intent.putExtra("wallet_name", wallet_name);
                    context.startActivity(intent);
                }else{
                    // error in generating card
                    Toast.makeText(context, responseJSON.toString(), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                // couldn't parse response into JSON
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
