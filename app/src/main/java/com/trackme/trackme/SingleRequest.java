package com.trackme.trackme;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleRequest extends BaseFragment {

    private EditText cf;
    private CheckBox subscribe;
    private Button mButton;
    private String companyName = "Unknown Company";
    // private FirebaseFirestore db;
    private FirebaseAuth mAuth;


    public SingleRequest() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // db = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_request, container, false);
        cf = view.findViewById(R.id.userCF);
        subscribe = view.findViewById(R.id.subscribeBox);
        mButton = view.findViewById(R.id.sendSingle);
        buttonClick();
        return view;
    }

    private void buttonClick(){
        final DBRequestHandler dbRequestHandler = new DBRequestHandler();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                DocumentReference docRef;
                if(currentUser!=null) {
                    docRef = dao.getUserDocument(currentUser.getUid());
                    // OLD
                    // docRef = db.collection("users").document(currentUser.getUid());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document!= null && document.exists()) {
                                    Map<String,Object> map = document.getData();
                                    if(map != null) {
                                        if(map.get("Name") != null){
                                            companyName = Objects.requireNonNull(map.get("Name")).toString();
                                            dbRequestHandler.generateNewRequest(cf.getText().toString().toUpperCase(),Boolean.toString(subscribe.isChecked()),companyName);
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
        });
    }

}
