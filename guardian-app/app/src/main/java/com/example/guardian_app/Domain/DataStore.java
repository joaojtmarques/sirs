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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;

public class DataStore implements Parcelable{

    private HashMap<String, String> _association;

    private HashMap<String, ArrayList<Float>> _safeZones;

    private static PublicKey publicKey;
    private static PrivateKey privateKey;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public DataStore() {
        _association = new HashMap<String, String>();
        _safeZones = new HashMap<String, ArrayList<Float>>();
        createKeys();
    }


    protected DataStore(Parcel in) {
        _association = (HashMap) in.readValue(HashMap.class.getClassLoader());
        _safeZones = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_association);
        dest.writeValue(_safeZones);
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




    public void addAssociation(String childName, String id) {
        _association.put(childName, id);
    }

    public String getAssociationByChildName(String childName) {
        return _association.get(childName);
    }

    public Set<String> getChildNames() {
        return _association.keySet();
    }

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


    public void addSafeZone(String childName, ArrayList<Float> safeZone) {
        _safeZones.put(childName, safeZone);
    }

    public ArrayList<Float> getSafeZoneByChildName(String childname) {
        if (_safeZones != null) {
            return _safeZones.get(childname);
        }
        return null;
    }

    public boolean isSafeZoneMapEmpty() {
        return _safeZones.isEmpty();
    }

    public void removeSafeZone(String childName) {
        _safeZones.remove(childName);
    }

}