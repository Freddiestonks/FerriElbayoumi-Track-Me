package com.trackme.trackme;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DAO {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String output = "892424";
    //Request info
    private boolean done = false;
    private int averageWeight = 0;

    public final static String dbLastName = "Last Name";
    public final static String dbCF = "CF";
    public final static String dbName = "Name";
    public final static String dbPhone = "Phone Number";
    public final static String dbYear = "Year";
    public final static String dbMonth = "Month";
    public final static String dbDay = "Day";
    public final static String dbGender = "Gender";
    public final static String dbMale = "Male";
    public final static String dbFemale = "Female";
    public final static String EMPTY_STRING = "";
    public final static String dbLatitude = "Latitude";
    public final static String dbLongitude = "Longitude";
    public final static String dbHeight = "Height";
    public final static String dbWeight = "Weight";
    public final static String dbSteps = "Steps";
    public final static String dbBmi = "BMI";
    public final static Double EMPTY_VALUE_DOUBLE = -1.0;

    public DAO() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    //
    // READ
    //
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    public void signOut() {
        mAuth.signOut();
    }
    public Task getRequests(String cf) {
        return db.collection("requests")
                //TODO ocio qua cambia il CF
                .whereEqualTo("CF", cf)
                .whereEqualTo("ApprovalStatus", "Pending")
                .get();
    }
    public DocumentReference getUserDocument(String userId) {
        return db.collection("users").document(userId);
    }
    public Task getUsersWithAgeRange(int max_age, int min_age) {
        return db.collection("users")
                .whereLessThanOrEqualTo("Year",Calendar.getInstance().get(Calendar.YEAR)-min_age)
                .whereGreaterThanOrEqualTo("Year",Calendar.getInstance().get(Calendar.YEAR)-max_age-1)
                .get();
    }
    public Task getCurrentUserRequest() {
        return getRequestByUserId(mAuth.getUid());
    }
    public Task getUserByCf(String CF) {
        return db.collection("users")
                .whereEqualTo("CF", CF)
                .get();
    }
    private Task getRequestByUserId(String id) {
        return db.collection("requests")
                .whereEqualTo("UserID", id)
                .get();
    }
    public Task signInWithEmailAndPassword(String email,String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    //
    // CREATE
    //
    public Task createUserWithEmailAndPassword(String inputEmail, String inputPassword) {
        return mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword);
    }

    public Task generateNewUser(boolean business){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Map<String, Object> user = new HashMap<>();
        user.put(dbCF,EMPTY_STRING);
        user.put(dbName, EMPTY_STRING);
        user.put(dbLastName, EMPTY_STRING);
        user.put(dbPhone, EMPTY_STRING);
        user.put(dbDay, EMPTY_STRING);
        user.put(dbMonth, EMPTY_STRING);
        user.put(dbYear,EMPTY_STRING);
        user.put(dbGender,EMPTY_STRING);
        user.put(dbHeight,EMPTY_STRING);
        user.put(dbWeight,EMPTY_STRING);
        user.put(dbLatitude,EMPTY_VALUE_DOUBLE);
        user.put(dbLongitude,EMPTY_VALUE_DOUBLE);
        if (business){
            user.put("Type","Business");}
        else {
            user.put("Type","User");
        }
        // Add a new document with a generated ID
        return db.collection("users").document(currentUser.getUid()).set(user);
    }

    public Task generateNewRequest(String targetCF, String subscribe, String companyName){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Task result = null;
        if (currentUser != null) {
            Map<String, Object> request = new HashMap<>();
            request.put(dbCF,targetCF);
            request.put("UserID", currentUser.getUid());
            request.put("Subscribe", subscribe);
            request.put("ApprovalStatus", "Pending");
            request.put("Timestamp",Long.toString(System.currentTimeMillis()/1000));
            request.put("Name", companyName);
            // Add a new document with a generated ID
            result = db.collection("requests").document(currentUser.getUid()+targetCF).set(request);
        }
        return result;
    }

    //
    // UPDATE
    //
    public Task updateCurrentUserDB(String key, Object object){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> user = new HashMap<>();
            user.put(key, object.toString());
            return updateUserDB(currentUser.getUid(), user);
        }
        return null;
    }
    public Task updateCurrentUserDB(Map<String, Object> user){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            return updateUserDB(currentUser.getUid(), user);
        }
        return null;
    }

    public Task updateUserDB(String id, Map<String, Object> user) {
        Task result = db.collection("users").document(id).update(user);
        return result;
    }

    public Task updateRequestsDB(String key, Object object,String documentID){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Task result = null;
        if (currentUser != null) {
            Map<String, Object> request = new HashMap<>();
            request.put(key, object.toString());
            // Add a new document with a generated ID
            result = db.collection("requests").document(documentID).update(request);
        }
        return result;
    }


}
