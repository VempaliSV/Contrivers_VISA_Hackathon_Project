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
import com.example.merchantapp.LoginActivity;
import com.example.merchantapp.MLE.RSA;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.merchantapp.RegisterActivity.llProgressBarRegister;

/*
API call for SignUp (POST):
    Request: email, password, full_name, country, mobile_number
    Response:
        request required to verify mobile_number
*/

public class RequestRegister extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;
    String mobile_number = null;

    public RequestRegister(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;
        String email = strings[1];
        String password = strings[2];
        mobile_number = strings[3];
        String username = strings[4];
        String country_code = strings[5];
        String state = strings[6];
        String zip_code = strings[7];

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
            jsonParam.put("mobile_number", mobile_number);
            jsonParam.put("name", username);
            jsonParam.put("country", country_code);
            jsonParam.put("acquirerCountryCode", country_code);
            jsonParam.put("state", state);
            jsonParam.put("zipCode", zip_code);

            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(jsonParam.toString());
            outputStream.flush();
            outputStream.close();

            // getting the response status code
            statusCode = conn.getResponseCode();

            InputStream inputStream;
            if(statusCode >= 200 && statusCode < 400){
                inputStream = conn.getInputStream();
            }else{
                inputStream = conn.getErrorStream();
            }

            // reading the response JSON and storing in "response"
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
        Activity register = (Activity)context;
        llProgressBarRegister.setVisibility(View.GONE);
        register.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {

                // get the response JSON
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 201){ // user created
                    RSA cipher = new RSA(context);
                    try {
                        // calling the otp activity for mobile number verification
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("mobile_number", mobile_number);
                        context.startActivity(intent);
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }else{ // error in creating user
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
