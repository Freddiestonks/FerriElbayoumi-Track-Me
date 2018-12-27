package com.trackme.trackme;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Button update_info;
    private EditText cf;
    private EditText phoneNumber;
    private EditText name;
    private EditText lastName;
    private TextView phoneNumberNavigator;
    private final static String dbLastName = "Last Name";
    private final static String dbCF = "CF";
    private final static String dbName = "Name";
    private final static String dbPhone = "Phone Number";

    public UserInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        update_info = view.findViewById(R.id.update_info);
        cf = view.findViewById(R.id.CF);
        phoneNumber = view.findViewById(R.id.phone_number);
        name = view.findViewById(R.id.Name);
        lastName = view.findViewById(R.id.Last_Name);
        buttonClick();
        filler();
        return view;
    }

    private void filler(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = null;

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
                                    if(map.get(dbCF) != null){
                                        cf.setText(map.get(dbCF).toString());}
                                    if(map.get(dbLastName) != null){
                                        lastName.setText(map.get(dbLastName).toString());
                                    }
                                    if(map.get(dbName) != null){
                                        name.setText(map.get(dbName).toString());
                                    }
                                    if(map.get(dbPhone) != null){
                                        phoneNumber.setText(map.get(dbPhone).toString());

                                       // phoneNumberNavigator.setText(map.get(dbPhone).toString());
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

    private void buttonClick(){

        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    Map<String, Object> user = new HashMap<>();
                    user.put(dbCF, cf.getText().toString());
                    user.put(dbName, name.getText().toString());
                    user.put(dbLastName, lastName.getText().toString());
                    user.put(dbPhone, phoneNumber.getText().toString());

// Add a new document with a generated ID
                    db.collection("users").document(currentUser.getUid())
                            .set(user)
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
        });

    }

}
