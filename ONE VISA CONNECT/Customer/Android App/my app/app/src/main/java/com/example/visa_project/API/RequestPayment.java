package com.example.visa_project.API;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import android.content.Context;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.visa_project.PaymentResultActivity.failImageView;
import static com.example.visa_project.PaymentResultActivity.llProgressBar;
import static com.example.visa_project.HomeActivity.session;
import static com.example.visa_project.PaymentResultActivity.statusTextView;
import static com.example.visa_project.PaymentResultActivity.successImageView;

/*
API request to process payments (PUT):
    Request: required_payload (for mVisa)
    Response:
        response on payment status
*/

public class RequestPayment extends AsyncTask<String, Void, String> {
    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;

    public RequestPayment(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;

        // payload for mVisa
        String payload = strings[1];
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + session.getAccessToken());
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // sending request body
            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(payload);
            outputStream.flush();
            outputStream.close();

            // get status code
            statusCode = conn.getResponseCode();

            InputStream inputStream;
            if(statusCode >= 200 && statusCode < 400){
                inputStream = conn.getInputStream();
            }else{
                inputStream = conn.getErrorStream();
            }

            // read response
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPostExecute(String flag) {
        super.onPostExecute(flag);
        llProgressBar.setVisibility(View.GONE);
        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 200){
                    // payment successful
                    successImageView.animate().alpha(1).setDuration(1000);
                    statusTextView.setText("Payment Successful");
                    statusTextView.animate().alpha(1).setDuration(1000);
                }else{
                    // payment failed
                    failImageView.animate().alpha(1).setDuration(1000);
                    statusTextView.setText("Payment Failed");
                    statusTextView.animate().alpha(1).setDuration(1000);
                }
            }catch (Exception e){
                // couldn't parse response into JSON
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
