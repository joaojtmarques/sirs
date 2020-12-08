package com.example.guardian_app.Domain;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;

public class DataStore implements Parcelable {

    private HashMap<String, String> _association;
    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    private static float _latitude;
    private static float _longitude;
    private static int _range;

    private static boolean _hasSafeZone;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataStore() {
        _association = new HashMap<String, String>();
        createKeys();
        deleteSafeZone();
    }

    public void addAssociation(String childName, String id) {
        _association.put(childName, id);
    }

    public String getAssociationByChildName(String childName) {
        return _association.get(childName);
    }

    public Set<String> getChildNames() {
        return _association.keySet();
    }

    protected DataStore(Parcel in) {
        _association = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_association);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataStore> CREATOR = new Parcelable.Creator<DataStore>() {
        @Override
        public DataStore createFromParcel(Parcel in) {
            return new DataStore(in);
        }

        @Override
        public DataStore[] newArray(int size) {
            return new DataStore[size];
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createKeys() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPublicKeyAsString() {
        return  Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getPrivateKeyAsString() {
        return  Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }


    public float getLatitude() {
        return _latitude;
    }

    public void setLatitude(float latitude) {
        _latitude = latitude;
    }

    public float getLongitude() {
        return _longitude;
    }

    public void setLongitude(float longitude) {
        _longitude = longitude;
    }

    public int getRange() {
        return _range;
    }

    public void setRange(int range) {
        _range = range;
    }

    public boolean hasSafeZoneDefined() {
        return _hasSafeZone;
    }

    public void defineSafeZone() {
        _hasSafeZone = true;
    }

    public void deleteSafeZone() {
        _hasSafeZone = false;
    }
}