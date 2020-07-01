package com.example.visa_project.API;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.zxing.WriterException;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import static com.example.visa_project.GenerateQRActivity.qrImageView;
import static com.example.visa_project.GenerateQRActivity.smallerDimension;
import static com.example.visa_project.HomeActivity.session;
import static com.example.visa_project.GenerateQRActivity.llProgressBar;
import static com.example.visa_project.StartActivity.isoCodes;

/*
API request for getting user's PAN (GET):
    Request: access_token
    Response:
        PAN
*/

public class GetPan extends AsyncTask<String, Void, String> {

    int statusCode = 0;
    String response = null;
    @SuppressLint("StaticFieldLeak")
    Context context = null;
    String wallet_name = null;
    String amount = null;

    public GetPan(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String flag = null;

        // selected wallet
        wallet_name = strings[1];

        // amount
        amount = strings[2];

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

            // flag to check if we got the response or not
            flag = "ok";
        } catch (Exception e) {
            // couldn't make a connection
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    protected void onPostExecute(String flag) {
        super.onPostExecute(flag);
        Activity generateQR = (Activity)context;
        llProgressBar.setVisibility(View.GONE);
        generateQR.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if(flag == null){
            Toast.makeText(context, "Couldn't connect to the server....", Toast.LENGTH_SHORT).show();
        }else {
            try {

                // get the response JSON
                JSONObject responseJSON = new JSONObject(response);
                if(statusCode == 200){

                    // info to be filled in the QR code
                    String inputValue = null;

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("wallet_name", wallet_name);
                        jsonObject.put("mobile_number", session.getMobileNumber());
                        jsonObject.put("amount", amount);
                        jsonObject.put("senderCardExpiryDate", "2025-10"); // default
                        jsonObject.put("senderCurrencyCode", isoCodes.getCurrencyCode(session.getCountry()));
                        jsonObject.put("senderPrimaryAccountNumber", responseJSON.getString("pan"));

                        inputValue = jsonObject.toString();
                    } catch (Exception e){
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }

                    // ZXing library's QR encoder
                    QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, smallerDimension);

                    try{
                        // setting the encode data into the ImageView
                        Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                        qrImageView.setImageBitmap(bitmap);
                    }catch (WriterException e){
                        // error in setting Bitmap
                        e.printStackTrace();
                        Toast.makeText(context, "Couldn't create QR code", Toast.LENGTH_SHORT).show();
                    }
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
