package com.trackme.trackme;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class Requests extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private TextView total;
    private int requests = 0;
    private TextView company[] = new TextView[5];
    private Button accept[] = new Button[5];
    private Button deny[] = new Button[5];

    public Requests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        total = view.findViewById(R.id.totalRequests);
        company[0] = view.findViewById(R.id.companyName1);
        company[1] = view.findViewById(R.id.companyName2);
        company[2] = view.findViewById(R.id.companyName3);
        company[3] = view.findViewById(R.id.companyName4);
        company[4] = view.findViewById(R.id.companyName5);

        accept[0] = view.findViewById(R.id.accept1);
        accept[1] = view.findViewById(R.id.accept2);
        accept[2] = view.findViewById(R.id.accept3);
        accept[3] = view.findViewById(R.id.accept4);
        accept[4] = view.findViewById(R.id.accept5);

        deny[0] = view.findViewById(R.id.deny1);
        deny[1] = view.findViewById(R.id.deny2);
        deny[2] = view.findViewById(R.id.deny3);
        deny[3] = view.findViewById(R.id.deny4);
        deny[4] = view.findViewById(R.id.deny5);


        getRequests();
        return view;
    }

    private void getRequests(){

        if (getArguments() != null) {
            db.collection("requests")
                    .whereEqualTo("CF",getArguments().getString("CF"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.get("ApprovalStatus").toString().equals("Pending")) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        if (requests < 5) {
                                            if(document.get("Subscribe").equals("true")) {
                                                company[requests].setText(document.get("Name").toString()+ " (S)");
                                            }
                                            else {
                                                company[requests].setText(document.get("Name").toString());

                                            }
                                            setAcceptDeny(document.getId(), requests);
                                        }
                                        requests++;
                                    }
                                }
                                total.setText(Integer.toString(requests));
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }

    }

    private  void setAcceptDeny(final String documentID, final int requests){
        final DBRequestHandler dbRequestHandler = new DBRequestHandler();
        accept[requests].setClickable(true);
        deny[requests].setClickable(true);
        accept[requests].setEnabled(true);
        deny[requests].setEnabled(true);
        accept[requests].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRequestHandler.updateRequestsDB("ApprovalStatus","Accepted",documentID);
                accept[requests].setTextColor(Color.GREEN);
                accept[requests].setClickable(false);
                deny[requests].setClickable(false);
                accept[requests].setEnabled(false);
                deny[requests].setEnabled(false);
            }
        });
        deny[requests].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbRequestHandler.updateRequestsDB("ApprovalStatus","Denied",documentID);
                deny[requests].setTextColor(Color.RED);
                accept[requests].setClickable(false);
                deny[requests].setClickable(false);
                accept[requests].setEnabled(false);
                deny[requests].setEnabled(false);
            }
        });
    }



}
