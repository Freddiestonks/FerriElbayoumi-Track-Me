package com.trackme.trackme;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;

public class DAO {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public DAO() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
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

    public Task createUserWithEmailAndPassword(String inputEmail, String inputPassword) {
        return mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword);
    }


}
