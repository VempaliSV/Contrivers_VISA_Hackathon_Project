package com.example.visa_project;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import com.visa.mvisa.tlvparser.QrCodeParser;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONException;
import org.json.JSONObject;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import static com.example.visa_project.HomeActivity.session;
import static com.example.visa_project.StartActivity.isoCodes;

public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    public ZXingScannerView mScannerView;
    static int requestCode = 1;

    public String createPayload(String qrCodeParserResponseString){
        String payloadString = null;
        try {
            JSONObject qrCodeData = new JSONObject(qrCodeParserResponseString).getJSONObject("qrCodeData");

            JSONObject payload = new JSONObject();

            payload.put("businessApplicationId", "MP");
            payload.put("acquirerCountryCode", isoCodes.getCountryCodeNumeric(qrCodeData.getString("countryCode")));
            payload.put("recipientPrimaryAccountNumber", qrCodeData.getString("tag19"));
            payload.put("merchantCategoryCode", qrCodeData.getString("merchantCategoryCode"));
            payload.put("settlementServiceIndicator", "9");

            payload.put("amount", getIntent().getStringExtra("amount"));
            payload.put("senderName", session.getUsername());
            payload.put("wallet_name", getIntent().getStringExtra("wallet_name"));
            payload.put("mobile_number", session.getMobileNumber());
            payload.put("transactionCurrencyCode", isoCodes.getCurrencyCodeNumeric(session.getCountry()));

            JSONObject merchantAddress = new JSONObject();
            merchantAddress.put("city", qrCodeData.getString("cityName"));
            merchantAddress.put("country", qrCodeData.getString("countryCode"));

            JSONObject cardAcceptor = new JSONObject();
            cardAcceptor.put("name", qrCodeData.getString("merchantName"));
            cardAcceptor.put("idCode", qrCodeData.getString("tag18"));
            cardAcceptor.put("address", merchantAddress);

            payload.put("cardAcceptor", cardAcceptor);
            payload.put("acquiringBin", qrCodeData.getString("tag17"));

            payloadString = payload.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payloadString;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, requestCode);
        }
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        // Storing the result 
        String tlvString = rawResult.toString();

        //Initializing the parser
        QrCodeParser QrParser = new QrCodeParser();

        String qrCodeParserResponseString = new QrCodeParser().parseQrDataAsJson(tlvString);

        String payload = createPayload(qrCodeParserResponseString);    

        if(payload != null){
            Intent intent = new Intent();
            intent.putExtra("payload", payload);
            setResult(RESULT_OK, intent);
            finish();
        }

        mScannerView.resumeCameraPreview(this);
    }
}
