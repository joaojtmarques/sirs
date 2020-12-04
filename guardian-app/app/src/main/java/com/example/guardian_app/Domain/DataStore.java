package com.example.guardian_app.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Set;

public class DataStore implements Parcelable {

    private HashMap<String, String> _association;


    public DataStore() {
        _association = new HashMap<String, String>();
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
}