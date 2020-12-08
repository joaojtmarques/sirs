package com.example.guardian_app.Domain;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.guardian_app.RetrofitAPI.InfoRetreiverApi;
import com.example.guardian_app.RetrofitAPI.RetrofitCreator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CipherHandling extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static SecretKey decipherKey(String key, PrivateKey privateKey) {
        try {

            String decryptedKey = decrypt(Base64.getDecoder().decode(key.getBytes()), privateKey);

            byte[] keyAsByteArray = Base64.getDecoder().decode(decryptedKey.getBytes());

            return new SecretKeySpec(keyAsByteArray, 0, keyAsByteArray.length, "AES");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println("Deciphering AES key with RSA/ECB/PKCS1Padding Algorithm...");
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decipherData(SecretKey secretKey, String message) {
        try {
            final String CIPHER_ALGO = "AES/ECB/PKCS5Padding";
            System.out.println("Deciphering data with " + CIPHER_ALGO + "...");
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(message)));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }




}
