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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PastRequests extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView name;
    private TextView lastName;
    private TextView phone;
    private TextView gender;
    private TextView dob;
    private TextView weight;
    private TextView steps;
    private TextView height;
    private TextView bmi;
    private Button search;
    private EditText cf;
    private boolean first = true;
    private boolean accepted = false;

    public PastRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        name = view.findViewById(R.id.userName);
        lastName = view.findViewById(R.id.userLastName);
        phone = view.findViewById(R.id.userPhone);
        gender = view.findViewById(R.id.userGender);
        dob = view.findViewById(R.id.userDOB);
        weight = view.findViewById(R.id.userWeight);
        steps = view.findViewById(R.id.userSteps);
        height = view.findViewById(R.id.userHeight);
        search = view.findViewById(R.id.searchRequestButton);
        cf = view.findViewById(R.id.cfRequests);
        bmi = view.findViewById(R.id.userBMI);
        buttonClick();


        return view;
    }

    private void buttonClick(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!cf.getText().toString().equals("")){
                   db.collection("requests")
                           //TODO ocio qua cambia il CF
                           .whereEqualTo("UserID",mAuth.getUid())
                           .get()
                           .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                               @Override
                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                   if (task.isSuccessful()) {
                                       for (QueryDocumentSnapshot document : task.getResult()) {
                                           if (document.get("ApprovalStatus").toString().equals("Accepted")&&document.get("CF").toString().equals(cf.getText().toString())&&document.get("UserID").toString().equals(mAuth.getUid())) {
                                               Log.d(TAG, document.getId() + " => " + document.getData());
                                               accepted = true;
                                               if(document.get("Subscribe").toString().equals("false")){
                                                   DBRequestHandler dbRequestHandler = new DBRequestHandler();
                                                   dbRequestHandler.updateRequestsDB("ApprovalStatus","Pending",document.getId());

                                               }
                                           }
                                           if(accepted) {
                                               db.collection("users")
                                                       .whereEqualTo("CF", cf.getText().toString())
                                                       .get()
                                                       .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                               if (task.isSuccessful()) {
                                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                                       if (first) {
                                                                           name.setText(document.get("Name").toString());
                                                                           lastName.setText(document.get("Last Name").toString());
                                                                           gender.setText(document.get("Gender").toString());
                                                                           height.setText(document.get("Height").toString());
                                                                           steps.setText(document.get("Steps").toString());
                                                                           weight.setText(document.get("Weight").toString());
                                                                           dob.setText(document.get("Day").toString() + "/" +
                                                                                   document.get("Month").toString() +
                                                                                   "/" + document.get("Year").toString());
                                                                           bmi.setText(document.get("BMI").toString());
                                                                           phone.setText(document.get("Phone Number").toString());
                                                                           first = false;
                                                                       }
                                                                   }
                                                                   first = true;
                                                                   accepted = false;
                                                               } else {
                                                                   Log.d(TAG, "Error getting documents: ", task.getException());
                                                               }
                                                           }
                                                       });
                                           }
                                       }
                                   } else {
                                       Log.d(TAG, "Error getting documents: ", task.getException());
                                   }
                               }
                           });

               }
            }
        });
    }

}
