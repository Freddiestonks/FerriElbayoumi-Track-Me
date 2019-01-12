package com.trackme.trackme;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class DBRequestHandler {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String output = "892424";
    //Request info
    private boolean done = false;
    private int averageWeight = 0;

    private final static String dbLastName = "Last Name";
    private final static String dbCF = "CF";
    private final static String dbName = "Name";
    private final static String dbPhone = "Phone Number";
    private final static String dbYear = "Year";
    private final static String dbMonth = "Month";
    private final static String dbDay = "Day";
    private final static String dbGender = "Gender";
    private final static String dbMale = "Male";
    private final static String dbFemale = "Female";
    private final static String EMPTY_STRING = "";
    private final static String dbLatitude = "Latitude";
    private final static String dbLongitude = "Longitude";
    private final static String dbHeight = "Height";
    private final static String dbWeight = "Weight";
    private final static Double EMPTY_VALUE_DOUBLE = -1.0;

    DBRequestHandler() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    void generateNewDB(boolean business){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        if (currentUser != null) {
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
            db.collection("users").document(currentUser.getUid()).set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //phoneNumberNavigator.setText(phoneNumber.getText().toString());
                            // Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
//
        }

    }

    public void generateRequest(String targetCF,String subscribe,String companyName){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        if (currentUser != null) {
            Map<String, Object> request = new HashMap<>();
            request.put(dbCF,targetCF);
            request.put("UserID", currentUser.getUid());
            request.put("Subscribe", subscribe);
            request.put("ApprovalStatus", "Pending");
            request.put("Timestamp",Long.toString(System.currentTimeMillis()/1000));
            request.put("Name", companyName);
            // Add a new document with a generated ID
            db.collection("requests").document(currentUser.getUid()+targetCF).set(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //phoneNumberNavigator.setText(phoneNumber.getText().toString());
                            // Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
//
        }

    }

    public void updateDB(String key, Object object){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> user = new HashMap<>();
            user.put(key, object.toString());
            // Add a new document with a generated ID
            db.collection("users").document(currentUser.getUid()).update(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //phoneNumberNavigator.setText(phoneNumber.getText().toString());
                            // Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
//
        }
    }

    public void updateRequestsDB(String key, Object object,String documentID){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Map<String, Object> request = new HashMap<>();
            request.put(key, object.toString());
            // Add a new document with a generated ID
            db.collection("requests").document(documentID).update(request)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //phoneNumberNavigator.setText(phoneNumber.getText().toString());
                            // Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
//
        }
    }

    public void getFromDBUser(final String key){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef;
        if(currentUser!=null) {
            docRef = db.collection("users").document(currentUser.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document!= null && document.exists()) {
                                Map<String,Object> map = document.getData();
                                if(map != null) {
                                    if(map.get(key) != null){
                                       output = Objects.requireNonNull(map.get(key)).toString();
                                    }
                                }
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
            });
        }
    }

    public void getDBAverageWeight(int max_age, int min_age, LatLng max,LatLng min){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef;
        final ArrayList<Integer> weight = new ArrayList<Integer>();
        if(currentUser!=null) {
            db.collection("users")
                    .whereLessThan("Year",Calendar.getInstance().get(Calendar.YEAR)-min_age)
                    .whereGreaterThan("Year",Calendar.getInstance().get(Calendar.YEAR)-max_age)
//                    .whereGreaterThan("Latitude",min.latitude)
//                    .whereGreaterThan("Longitude",min.longitude)
//                    .whereLessThan("Latitude",max.latitude)
//                    .whereLessThan("Longitude",max.longitude)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    if(!document.getData().get("Weight").toString().equals("")) {
                                        weight.add(Integer.parseInt(document.getData().get("Weight").toString()));
                                    }
                                }
                                for(int i = 0; i<weight.size();i++){
                                    averageWeight = averageWeight + weight.get(i)/weight.size();
                                }


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }


    }

    public String getDbLastName() {
        return dbLastName;
    }

    public String getDbCF() {
        return dbCF;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbPhone() {
        return dbPhone;
    }

    public String getDbYear() {
        return dbYear;
    }

    public String getDbMonth() {
        return dbMonth;
    }

    public String getDbDay() {
        return dbDay;
    }

    public String getDbGender() {
        return dbGender;
    }

    public String getDbMale() {
        return dbMale;
    }

    public String getDbFemale() {
        return dbFemale;
    }

    public String getDbLatitude() {
        return dbLatitude;
    }

    public String getDbLongitude() {
        return dbLongitude;
    }

    public String getDbHeight() {
        return output;
    }

    public String getDbWeight() {
        return dbWeight;
    }

    public int getAverageWeight() {
        return averageWeight;
    }

}
