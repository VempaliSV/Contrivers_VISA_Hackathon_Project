package com.example.merchantapp.API;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.merchantapp.HistoryRecycleViewHolder.PaymentCardRecyclerViewAdapter;
import com.example.merchantapp.Network.PaymentEntry;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.merchantapp.HistoryActivity.recyclerView;
import static com.example.merchantapp.HomeActivity.session;
import static com.example.merchantapp.HistoryActivity.llProgressBar;

/*
API request to fetch transaction history of the User (GET):
    Request: access_token
    Response:
        List of all the transactions
*/

public class RequestHistory extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;

    public RequestHistory(Context context) {
        this.context = context;
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

    @Override
    public void onPostExecute(String flag) {
        super.onPostExecute(flag);
        Activity historyActivity = (Activity)context;
        llProgressBar.setVisibility(View.GONE);
        historyActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(flag == null){
            Toast.makeText(context, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
        }else {
            try {
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 200){
                    // get a list of histories in JSON format
                    JSONArray historyArray = responseJSON.getJSONArray("history");
                    // formatting the histories (according to transaction time)
                    JSONArray sortedHistoryArray = new JSONArray();
                    for (int i = historyArray.length()-1; i>=0; i--) {
                        sortedHistoryArray.put(historyArray.get(i));
                    }

                    String history = sortedHistoryArray.toString();

                    // setting the UI to show histories
                    PaymentCardRecyclerViewAdapter adapter = new PaymentCardRecyclerViewAdapter(
                            PaymentEntry.initPaymentEntryList(history));
                    recyclerView.setAdapter(adapter);
                }else{
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
