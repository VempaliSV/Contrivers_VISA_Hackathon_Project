package com.example.visa_project.MLE;
import android.os.Build;
import androidx.annotation.RequiresApi;
import java.io.*;
import java.security.Key;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import android.content.Context;
import android.widget.Toast;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

/*
RSA for message level encryption:
*/

@RequiresApi(api = Build.VERSION_CODES.O)
public class RSA {
    public static final String NEW_LINE_CHARACTER = "\n";
    public static final String PUBLIC_KEY_START_KEY_STRING = "-----BEGIN PUBLIC KEY-----";
    public static final String PUBLIC_KEY_END_KEY_STRING = "-----END PUBLIC KEY-----";
    public static final String PRIVATE_KEY_START_KEY_STRING = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String PRIVATE_KEY_END_KEY_STRING = "-----END RSA PRIVATE KEY-----";
    public static final String EMPTY_STRING = "";
    Context context;

    public RSA(Context context){
        this.context = context;
    }

    // encryption using RSA (public key)
    public String encrypt(String input) throws Throwable {

        byte[] inputByteArray = input.getBytes();
        String keyString = "";

        // reading the public key
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("public.pem"), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                keyString += line;
            }
            reader.close();
        }catch (IOException e){
            Toast.makeText(context, "Couldn't create file reader", Toast.LENGTH_SHORT).show();
        }

        // fetching the key from public key
        keyString = keyString.replaceAll(NEW_LINE_CHARACTER, EMPTY_STRING)
                .replaceAll(PUBLIC_KEY_START_KEY_STRING, EMPTY_STRING)
                .replaceAll(PUBLIC_KEY_END_KEY_STRING, EMPTY_STRING);

        byte[] publicKey = keyString.getBytes();

        Key generatePublic = KeyFactory.getInstance("RSA").
                generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));

        // create RSA Cipher instance
        Cipher cipherInstance = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipherInstance.init(Cipher.ENCRYPT_MODE, generatePublic);

        // encrypt string
        byte[] encryptedByteArray = cipherInstance.doFinal(inputByteArray);
        return Base64.getEncoder().encodeToString(encryptedByteArray);
    }

    public String decrypt(String input) throws Throwable {

        // byte array from response
        byte[] inputByteArray = Base64.getDecoder().decode(input);
        String keyString = "";

        // read the private key
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("private.pem"), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                keyString += line;
            }
            reader.close();
        }catch (IOException e){
            Toast.makeText(context, "Couldn't create file reader", Toast.LENGTH_SHORT).show();
        }

        // fetching the key from public key
        keyString = keyString.replaceAll(NEW_LINE_CHARACTER, EMPTY_STRING)
                .replaceAll(PRIVATE_KEY_START_KEY_STRING, EMPTY_STRING)
                .replaceAll(PRIVATE_KEY_END_KEY_STRING, EMPTY_STRING);

        byte[] privateKey = keyString.getBytes();

        Key generatePrivate = KeyFactory.getInstance("RSA").
                generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));

        // create RSA Cipher instance
        Cipher cipherInstance = Cipher.getInstance("RSA/None/PKCS1Padding");
        cipherInstance.init(Cipher.DECRYPT_MODE, generatePrivate);

        // encrypt string
        byte[] decryptedByteArray = cipherInstance.doFinal(inputByteArray);
        return new String(decryptedByteArray);
    }
}
