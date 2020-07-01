package com.example.merchantapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.visa.mvisa.QRCodeTag;
import com.visa.mvisa.generator.InputInvalidException;
import com.visa.mvisa.generator.QrCodeDataGenerator;

import java.util.HashMap;
import static com.example.merchantapp.HomeActivity.session;

public class GenerateQRActivity extends AppCompatActivity {

    static TextView textView;
    static ImageView imageView;

    private static String testEMVMapBasedGenerator() {
        String tlv;
        HashMap<String, Object> a = createMerchantDataRequestMap();
        try {
            tlv = QrCodeDataGenerator.generateQrCodeData(a);
        } catch (InputInvalidException exception) {
            tlv = "error";
            exception.getStackTrace();
        }
        return tlv;
    }

    private static HashMap<String, Object> createMerchantDataRequestMap() {
        //Request object
        HashMap<String, Object> merchantQrDataRequest = new HashMap<>();
        //populate request data
        merchantQrDataRequest.put(QRCodeTag.PAYLOAD_FORMAT_INDICATOR.tagCode, "01");
        merchantQrDataRequest.put(QRCodeTag.COUNTRY_CODE.tagCode, "IN");
        merchantQrDataRequest.put(QRCodeTag.MERCHANT_CATEGORY_CODE.tagCode, "5812");
        merchantQrDataRequest.put(QRCodeTag.MERCHANT_PAN.tagCode,"4123640062698797");
        merchantQrDataRequest.put(QRCodeTag.MERCHANT_NAME.tagCode,session.getUsername());
        merchantQrDataRequest.put(QRCodeTag.CURRENCY_CODE.tagCode, "356");
        merchantQrDataRequest.put(QRCodeTag.MERCHANT_ID.tagCode, "47613617");
        merchantQrDataRequest.put(QRCodeTag.CITY_NAME.tagCode, "CHENNAI");
        merchantQrDataRequest.put(QRCodeTag.TAG_17.tagCode,"408972");
        merchantQrDataRequest.put(QRCodeTag.TAG_18.tagCode,"CA-IDCode-77765");
        merchantQrDataRequest.put(QRCodeTag.TAG_19.tagCode,"4123640062698797");

        return merchantQrDataRequest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_q_r);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.qrCodeImageView);
        String tlv = testEMVMapBasedGenerator();

        if(tlv.equals("Error")){
            textView.setText("Error generating the QR code");
        }
        else{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                BitMatrix bitMatrix = multiFormatWriter.encode(tlv, BarcodeFormat.QR_CODE,200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                imageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

    }
}