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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.type.Date;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private Button update_info;
    private EditText cf;
    private EditText phoneNumber;
    private EditText name;
    private EditText lastName;
    private EditText day;
    private EditText month;
    private EditText year;
    private RadioGroup gender;
    private RadioButton male;
    private RadioButton female;
    private TextView date;
    private String type;
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private final static String dbLastName = "Last Name";
    private final static String dbCF = "CF";
    private final static String dbName = "Name";
    private final static String dbPhone = "Phone Number";
    private final static String dbYear = "Year";
    private final static String dbMonth = "Month";
    private final static String dbday = "Day";
    private final static String dbGender = "Gender";
    private final static String dbMale = "Male";
    private final static String dbFemale = "Female";
    private final static String dbWeight = "Weight";
    private final static String dbHeight = "Height";




    public UserInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        update_info = view.findViewById(R.id.update_info);
        cf = view.findViewById(R.id.CF);
        phoneNumber = view.findViewById(R.id.phone_number);
        name = view.findViewById(R.id.Name);
        lastName = view.findViewById(R.id.Last_Name);
        day = view.findViewById(R.id.day);
        month = view.findViewById(R.id.month);
        year = view.findViewById(R.id.year);
        gender = view.findViewById(R.id.gender);
        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);
        date = view.findViewById(R.id.textViewUI);
        //cf.setText(getArguments().getString(dbCF));
        //honeNumber.setText(getArguments().getString(dbPhone));
        buttonClick();
        setter();
        filler();
        return view;
    }

    private void setter(){
        if (getArguments() != null){
            cf.setText(getArguments().getString(dbCF));
            day.setText(getArguments().getString(dbday));
            month.setText(getArguments().getString(dbMonth));
            year.setText(getArguments().getString(dbYear));
            name.setText(getArguments().getString(dbName));
            lastName.setText(getArguments().getString(dbLastName));
            phoneNumber.setText(getArguments().getString(dbPhone));
            if(getArguments().getString("Type").equals("Business")) {
                day.setVisibility(View.INVISIBLE);
                month.setVisibility(View.INVISIBLE);
                year.setVisibility(View.INVISIBLE);
                date.setVisibility(View.INVISIBLE);
                gender.setVisibility(View.INVISIBLE);
                lastName.setVisibility(View.INVISIBLE);
            }
            String gen = getArguments().getString(dbGender);
            if (gen.equals(dbMale)){
                male.setChecked(true);
                female.setChecked(false);
            }
            else if(gen.equals(dbFemale)){
                male.setChecked(false);
                female.setChecked(true);}
            else {
                male.setChecked(false);
                female.setChecked(false);
            }

        }
    }

    private void filler(){
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
                                    if(map.get(dbCF) != null){
                                        cf.setText(map.get(dbCF).toString());
                                        //TODO FIX NULL POINTER
                                        if (getActivity() != null)
                                            ((User) getActivity()).setCf(map.get(dbCF).toString());
                                    }
                                    if(map.get(dbLastName) != null){
                                        lastName.setText(map.get(dbLastName).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setLastName(map.get(dbLastName).toString());

                                    }
                                    if(map.get("Type") != null){
                                        if(map.get("Type").equals("Business")) {
                                            day.setVisibility(View.INVISIBLE);
                                            month.setVisibility(View.INVISIBLE);
                                            year.setVisibility(View.INVISIBLE);
                                            date.setVisibility(View.INVISIBLE);
                                            gender.setVisibility(View.INVISIBLE);
                                            lastName.setVisibility(View.INVISIBLE);
                                        }
                                        if (getActivity() != null)
                                            ((User) getActivity()).setType(map.get("Type").toString());


                                    }
                                    if(map.get(dbName) != null){
                                        name.setText(map.get(dbName).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setName(map.get(dbName).toString());

                                    }
                                    if(map.get(dbPhone) != null){
                                        phoneNumber.setText(map.get(dbPhone).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setPhoneNumber(map.get(dbPhone).toString());

                                        // phoneNumberNavigator.setText(map.get(dbPhone).toString());
                                    }
                                    if(map.get(dbday) != null){
                                        day.setText(map.get(dbday).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setDay(map.get(dbday).toString());

                                        // phoneNumberNavigator.setText(map.get(dbPhone).toString());
                                    }
                                    if(map.get(dbYear) != null){
                                        year.setText(map.get(dbYear).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setYear(map.get(dbYear).toString());

                                        // phoneNumberNavigator.setText(map.get(dbPhone).toString());
                                    }
                                    if(map.get(dbMonth) != null){
                                        month.setText(map.get(dbMonth).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setMonth(map.get(dbMonth).toString());
                                    }
                                    if(map.get(dbGender) != null){
                                        String gen = map.get(dbGender).toString();
                                        if (getActivity() != null)
                                            ((User) getActivity()).setGender(map.get(dbGender).toString());
                                        if (gen.equals(dbMale)){
                                            male.setChecked(true);
                                            female.setChecked(false);
                                        }
                                        else if(gen.equals(dbFemale)){
                                            male.setChecked(false);
                                            female.setChecked(true);}
                                            else {
                                            male.setChecked(false);
                                            female.setChecked(false);
                                        }
                                    }
                                    if(map.get(dbWeight) != null){
                                        if (getActivity() != null)
                                            ((User) getActivity()).setWeight(map.get(dbWeight).toString());

                                        // phoneNumberNavigator.setText(map.get(dbPhone).toString());
                                    }
                                    if(map.get(dbHeight) != null){
                                        if (getActivity() != null)
                                            ((User) getActivity()).setHeight(map.get(dbHeight).toString());

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
                    if((!day.getText().toString().equals(""))&&( 1 <= Integer.parseInt(day.getText().toString()))&&(Integer.parseInt(day.getText().toString())<=31)){
                        user.put(dbday, Integer.parseInt(day.getText().toString()));
                    }
                    if((!month.getText().toString().equals("")) && (1 <= Integer.parseInt(month.getText().toString())) && (Integer.parseInt(month.getText().toString()) <= 12)){
                        user.put(dbMonth, Integer.parseInt(month.getText().toString()));
                    }
                    if( (!year.getText().toString().equals(""))&&1900 <= Integer.parseInt(year.getText().toString())&&Integer.parseInt(year.getText().toString())<=currentYear){
                        user.put(dbYear, Integer.parseInt(year.getText().toString()));
                    }
                    if(gender.getCheckedRadioButtonId() == male.getId()){
                        user.put(dbGender, dbMale);
                    }
                    else if(gender.getCheckedRadioButtonId() == female.getId()){
                        user.put(dbGender, dbFemale);
                    }

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
        });
    }

}
