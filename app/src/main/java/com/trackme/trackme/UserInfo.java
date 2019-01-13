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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfo extends BaseFragment {

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
    int currentYear = Calendar.getInstance().get(Calendar.YEAR);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        // Inflate the layout for this fragment
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
        buttonClick();
        setter();
        filler();
        return view;
    }

    private void setter(){
        if (getArguments() != null){
            cf.setText(getArguments().getString(DAO.dbCF));
            day.setText(getArguments().getString(DAO.dbDay));
            month.setText(getArguments().getString(DAO.dbMonth));
            year.setText(getArguments().getString(DAO.dbYear));
            name.setText(getArguments().getString(DAO.dbName));
            lastName.setText(getArguments().getString(DAO.dbLastName));
            phoneNumber.setText(getArguments().getString(DAO.dbPhone));
            if(getArguments().getString("Type").equals("Business")) {
                day.setVisibility(View.INVISIBLE);
                month.setVisibility(View.INVISIBLE);
                year.setVisibility(View.INVISIBLE);
                date.setVisibility(View.INVISIBLE);
                gender.setVisibility(View.INVISIBLE);
                lastName.setVisibility(View.INVISIBLE);
            }
            String gen = getArguments().getString(DAO.dbGender);
            if (gen.equals(DAO.dbMale)){
                male.setChecked(true);
                female.setChecked(false);
            }
            else if(gen.equals(DAO.dbFemale)){
                male.setChecked(false);
                female.setChecked(true);}
            else {
                male.setChecked(false);
                female.setChecked(false);
            }

        }
    }

    private void filler(){
        FirebaseUser currentUser = dao.getCurrentUser();
        DocumentReference docRef;

        if(currentUser!=null) {
            docRef = dao.getUserDocument(currentUser.getUid());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document!= null && document.exists()) {
                                Map<String,Object> map = document.getData();
                                if(map != null) {
                                    if(map.get(DAO.dbCF) != null){
                                        cf.setText(map.get(DAO.dbCF).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setCf(map.get(DAO.dbCF).toString());
                                    }
                                    if(map.get(DAO.dbLastName) != null){
                                        lastName.setText(map.get(DAO.dbLastName).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setLastName(map.get(DAO.dbLastName).toString());

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
                                    if(map.get(DAO.dbName) != null){
                                        name.setText(map.get(DAO.dbName).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setName(map.get(DAO.dbName).toString());

                                    }
                                    if(map.get(DAO.dbPhone) != null){
                                        phoneNumber.setText(map.get(DAO.dbPhone).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setPhoneNumber(map.get(DAO.dbPhone).toString());
                                    }
                                    if(map.get(DAO.dbDay) != null){
                                        day.setText(map.get(DAO.dbDay).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setDay(map.get(DAO.dbDay).toString());
                                    }
                                    if(map.get(DAO.dbYear) != null){
                                        year.setText(map.get(DAO.dbYear).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setYear(map.get(DAO.dbYear).toString());
                                    }
                                    if(map.get(DAO.dbMonth) != null){
                                        month.setText(map.get(DAO.dbMonth).toString());
                                        if (getActivity() != null)
                                            ((User) getActivity()).setMonth(map.get(DAO.dbMonth).toString());
                                    }
                                    if(map.get(DAO.dbGender) != null){
                                        String gen = map.get(DAO.dbGender).toString();
                                        if (getActivity() != null)
                                            ((User) getActivity()).setGender(map.get(DAO.dbGender).toString());
                                        if (gen.equals(DAO.dbMale)){
                                            male.setChecked(true);
                                            female.setChecked(false);
                                        }
                                        else if(gen.equals(DAO.dbFemale)){
                                            male.setChecked(false);
                                            female.setChecked(true);}
                                            else {
                                            male.setChecked(false);
                                            female.setChecked(false);
                                        }
                                    }
                                    if(map.get(DAO.dbWeight) != null){
                                        if (getActivity() != null)
                                            ((User) getActivity()).setWeight(map.get(DAO.dbWeight).toString());
                                    }
                                    if(map.get(DAO.dbHeight) != null){
                                        if (getActivity() != null)
                                            ((User) getActivity()).setHeight(map.get(DAO.dbHeight).toString());
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(), "Document not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Error in getting user document!\n" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        }
    }

    private void buttonClick(){

        update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = dao.getCurrentUser();
                if (currentUser != null) {
                    Map<String, Object> user = new HashMap<>();
                    user.put(DAO.dbCF, cf.getText().toString().toUpperCase());
                    user.put(DAO.dbName, name.getText().toString());
                    user.put(DAO.dbLastName, lastName.getText().toString());
                    user.put(DAO.dbPhone, phoneNumber.getText().toString());
                    if((!day.getText().toString().equals(""))&&( 1 <= Integer.parseInt(day.getText().toString()))&&(Integer.parseInt(day.getText().toString())<=31)){
                        user.put(DAO.dbDay, Integer.parseInt(day.getText().toString()));
                    }
                    if((!month.getText().toString().equals("")) && (1 <= Integer.parseInt(month.getText().toString())) && (Integer.parseInt(month.getText().toString()) <= 12)){
                        user.put(DAO.dbMonth, Integer.parseInt(month.getText().toString()));
                    }
                    if( (!year.getText().toString().equals(""))&&1900 <= Integer.parseInt(year.getText().toString())&&Integer.parseInt(year.getText().toString())<=currentYear){
                        user.put(DAO.dbYear, Integer.parseInt(year.getText().toString()));
                    }
                    if(gender.getCheckedRadioButtonId() == male.getId()){
                        user.put(DAO.dbGender, DAO.dbMale);
                    }
                    else if(gender.getCheckedRadioButtonId() == female.getId()){
                        user.put(DAO.dbGender, DAO.dbFemale);
                    }

                    dao.updateUserDB(currentUser.getUid(), user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "User's info updated!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error in updating user's info!\n" + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
//
                }
            }
        });
    }

}
