package com.example.merchantapp.API;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.example.merchantapp.HomeActivity;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.merchantapp.LoginActivity.llProgressBar;
import static com.example.merchantapp.LoginActivity.session;

/*
API call for LogIn (POST):
    Request: email, password
    Response:
        access_token
        refresh_token
        user_details
*/

public class RequestLogin extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;

    public RequestLogin(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;
        String email = strings[1];
        String password = strings[2];
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // JSON for request body
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("email", email);
            jsonParam.put("password", password);

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(jsonParam.toString());
            outputStream.flush();
            outputStream.close();

            // getting the response status code
            statusCode = conn.getResponseCode();

            // reading the response JSON and storing in "response"
            InputStream inputStream;
            if(statusCode >= 200 && statusCode < 400){
                inputStream = conn.getInputStream();
            }else{
                inputStream = conn.getErrorStream();
            }

            int chr;
            response = "";
            while ((chr = inputStream.read()) != -1) {
                response += (char) chr;
            }
            inputStream.close();

            conn.disconnect();

            // flag to check if everything worked fine
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
        Activity login = (Activity)context;
        llProgressBar.setVisibility(View.GONE);
        login.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {
                // get the response JSON
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 200){

                    // storing the access_token and refresh_token in shared_preferences
                    String access_token = responseJSON.getString("access_token");
                    String refresh_token = responseJSON.getString("refresh_token");
                    session.createLoginSession(access_token, refresh_token);

                    // storing the user details in shared_preferences
                    JSONObject merchant = responseJSON.getJSONObject("merchant");
                    String email = merchant.getString("email");
                    String username = merchant.getString("name");
                    String mobile_number = merchant.getString("mobile_number");
                    String country = merchant.getString("country");
                    String state = merchant.getString("state");
                    String zip_code = merchant.getString("zipCode");
                    session.createUserSession(username, email, mobile_number, country, state, zip_code);

                    Intent newIntent = new Intent(context, HomeActivity.class);
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(newIntent);
                }else{
                    // error on backend
                    Toast.makeText(context, responseJSON.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                // couldn't parse response into JSON
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}
