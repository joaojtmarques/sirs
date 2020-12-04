package com.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class DataStore {

    // max of locations stored per child
    private int maxLocations = 5;

    // maps guardian's public key to a requested association's id
    private HashMap<String, String> _requestedAssociations = new HashMap<>();

    // maps association id to public key of who has access
    private HashMap<String, String> _associations = new HashMap<>();

    // maps association id to child's data
    private HashMap<String, LinkedList<String>> _data = new HashMap<>();

    public String getRequestedAssociation(String pubKey) {
        return _requestedAssociations.get(pubKey);
    }

    public void addRequestedAssociation(String pubKey, String associationId) {
        _requestedAssociations.put(pubKey, associationId);
    }

    public Boolean hasRequestedAssociation(String pubKey) {
        return _requestedAssociations.containsKey(pubKey);
    }

    public void removeRequestedAssociation(String pubKey) {
        _requestedAssociations.remove(pubKey);
    }

    public String getAssociation(String associationId) {
        return _associations.get(associationId);
    }

    public void addAssociation(String associationId, String pubKey) {
        _associations.put(associationId, pubKey);
    }

    public Boolean hasAssociation(String associationId) {
        return _associations.containsKey(associationId);
    }

    public void addLocationData(String associationId, String data) {
        if (_data.get(associationId) == null ) {
            _data.put(associationId, new LinkedList<>());
        }
        if (_data.get(associationId).size() == maxLocations) {
            _data.get(associationId).remove();
        }
        _data.get(associationId).add(data);
    }

    public ArrayList<String> getData(String associationId) {
        return new ArrayList<>(_data.get(associationId));
    }


}
