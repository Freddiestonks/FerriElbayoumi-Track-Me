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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_request, container, false);
        cf = view.findViewById(R.id.userCF);
        subscribe = view.findViewById(R.id.subscribeBox);
        mButton = view.findViewById(R.id.sendSingle);
        buttonClick();
        return view;
    }

    private boolean userFound = false;

    private void buttonClick(){
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = dao.getCurrentUser();
                DocumentReference docRef;
                userFound = true;

                // check if the target user CF exists
                dao.getUserByCf(cf.getText().toString().toUpperCase())
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    userFound = false;
                                    Toast.makeText(getActivity(), "No user found!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    userFound = true;
                                    Toast.makeText(getActivity(), "User found! Processing request..", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "ERROR IN CHECKING USER!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                if(currentUser!=null && userFound) {
                    docRef = dao.getUserDocument(currentUser.getUid());
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

                                            dao.generateNewRequest(cf.getText().toString().toUpperCase(),Boolean.toString(subscribe.isChecked()),companyName)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(getActivity(), "Request generated succesfully!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getActivity(), "Error while generating request!\n" + e, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Current user document not found!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Error in getting current user information!\n" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

}
