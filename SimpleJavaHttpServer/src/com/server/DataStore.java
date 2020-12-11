package com.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class DataStore {

    private int freeTrialUsages = 1;
    private int premiumUsages = 5;

    // max of locations stored per child
    private int maxLocations = 5;

    // maps a requested association's id to 1 if it exists - map for performance
    private HashMap<String, Integer> _requestedAssociations = new HashMap<>();

    // maps association ids to 1 if it exists - map for performance
    private HashMap<String, Integer> _associations = new HashMap<>();

    // maps association id to child's data
    private HashMap<String, LinkedList<String>> _data = new HashMap<>();

    // maps keys to remaining bind requests
    private HashMap<String, Integer> _keyUsage = new HashMap<>();

    public DataStore() {
        // simulate register and upgrade user for demo
        registerUser("FnJh43t8RHuF4");
        upgradeUser("FnJh43t8RHuF4");
    }

    /*public String getRequestedAssociation(String pubKey) {
        return _requestedAssociations.get(pubKey);
    }*/

    public Boolean addRequestedAssociation(String associationId, String premiumKey) {
        if (premiumKey != null && hasUsages(premiumKey)) {
            _requestedAssociations.put(associationId, 1);
            _keyUsage.put(premiumKey, _keyUsage.get(premiumKey) -1);
            return true;
        }
        return false;
    }

    public Boolean hasRequestedAssociation(String pubKey) {
        return _requestedAssociations.containsKey(pubKey);
    }

    public void removeRequestedAssociation(String associationId) {
        _requestedAssociations.remove(associationId);
    }

    public void addAssociation(String associationId) {
        _associations.put(associationId, 1);
    }

    public Boolean hasAssociation(String associationId) {
        return _associations.containsKey(associationId);
    }

    public void addLocationData(String associationId, String data) {
        _data.computeIfAbsent(associationId, k -> new LinkedList<>());
        if (_data.get(associationId).size() == maxLocations) {
            _data.get(associationId).remove();
        }
        _data.get(associationId).add(data);
    }

    public String getData(String associationId) {
        return _data.get(associationId).getLast();
    }

    public Boolean hasUsages(String premiumKey) {
        return _keyUsage.get(premiumKey) > 0;
    }

    public void registerUser(String key) {
        _keyUsage.put(key, freeTrialUsages);
    }

    public void upgradeUser(String userKey) {
        int remainingUsages = _keyUsage.get(userKey);
        _keyUsage.put(userKey, premiumUsages - remainingUsages);
    }
}
