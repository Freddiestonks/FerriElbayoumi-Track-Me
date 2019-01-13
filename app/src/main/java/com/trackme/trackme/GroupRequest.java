package com.trackme.trackme;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
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

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupRequest extends BaseFragment {

    private Button search;
    private EditText address;
    private EditText range;
    private EditText minAge;
    private EditText maxAge;
    private TextView avgWeight;
    private TextView avgHeight;
    private TextView avgAge;
    private TextView avgBMI;
    private TextView avgSteps;
    private TextView mFRate;

    private LatLng loc;
    private LatLng maxLoc;
    private LatLng minLoc;
    private int radius = 0;
    private double bmi_calc = 22;
    private int averageWeight = 0;
    private int averageHeight = 0;
    private int averageSteps = 0;
    private int averageAge = 0;
    private int male = 0;
    private int female = 0;
    private int people;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_request, container, false);
        search = view.findViewById(R.id.groupSearchButton);
        range = view.findViewById(R.id.radius);
        address = view.findViewById(R.id.streetAddress);
        minAge = view.findViewById(R.id.minAge);
        maxAge = view.findViewById(R.id.maxAge);
        avgWeight = view.findViewById(R.id.avgWeight);
        avgHeight = view.findViewById(R.id.avgHeight);
        avgAge = view.findViewById(R.id.avgAge);
        avgBMI = view.findViewById(R.id.avgBMI);
        avgSteps = view.findViewById(R.id.avgSteps);
        mFRate = view.findViewById(R.id.maleFemale);
        buttonClick();

        return view;
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return p1;
    }

    private void setMaxMinLoc(LatLng pos){
        Double loDiff;
        Double laDiff;
        Double loDiffPM = 1/111320.0;
        Double laDiffPM = 360/(40075000.0*Math.cos(loc.latitude)) ;
        laDiff = laDiffPM*radius;
        loDiff = loDiffPM*radius;
        maxLoc = new LatLng(pos.latitude+laDiff,pos.longitude + loDiff);
        minLoc = new LatLng(pos.latitude-laDiff,pos.longitude - loDiff);
    }
    private void buttonClick(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!range.getText().toString().equals("")){
                radius = Integer.parseInt(range.getText().toString());}
                else {
                    radius = 0;
                }
                loc = getLocationFromAddress(search.getContext(),address.getText().toString());
                if(loc !=null) {
                    setMaxMinLoc(loc);
                }else {
                    maxLoc = new LatLng(90,90);
                    minLoc = new LatLng(-90,-90);
                }
                int max_age = 200;
                int min_age = 0;
                if(!maxAge.getText().toString().equals("")) {
                    max_age = Integer.parseInt(maxAge.getText().toString());
                }
                if(!minAge.getText().toString().equals("")){
                    min_age = Integer.parseInt(minAge.getText().toString());
                }
                getDBAverageWeight(max_age,min_age,maxLoc,minLoc);
           }
        });
    }


    public void getDBAverageWeight(int max_age, int min_age, final LatLng max, final LatLng min){
        FirebaseUser currentUser = dao.getCurrentUser();
        averageWeight = 0;
        averageHeight = 0;
        averageSteps = 0;
        averageAge = 0;
        people = 0;
        male = 0;
        female = 0;
        final ArrayList<Integer> weight = new ArrayList<>();
        final ArrayList<Integer> height = new ArrayList<>();
        final ArrayList<Integer> steps = new ArrayList<>();
        final ArrayList<Integer> age = new ArrayList<>();
        if(currentUser!=null) {
            dao.getUsersWithAgeRange(max_age, min_age)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Double latitude = Double.parseDouble(document.getData().get(DAO.dbLatitude).toString());
                                Double longitude = Double.parseDouble(document.getData().get(DAO.dbLongitude).toString());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(latitude>=min.latitude && latitude<=max.latitude&& longitude>=min.longitude&&longitude<=max.longitude) {
                                    people++;
                                    if (document.getData().get(DAO.dbWeight)!=null&&!document.getData().get(DAO.dbWeight).toString().equals("")) {
                                        weight.add(Integer.parseInt(document.getData().get(DAO.dbWeight).toString()));
                                    }
                                    if (document.getData().get(DAO.dbHeight)!= null &&!document.getData().get(DAO.dbHeight).toString().equals("")) {
                                        height.add(Integer.parseInt(document.getData().get(DAO.dbHeight).toString()));
                                    }
                                    if (document.getData().get(DAO.dbSteps)!=null&&!document.getData().get(DAO.dbSteps).toString().equals("")) {
                                        steps.add(Integer.parseInt(document.getData().get(DAO.dbSteps).toString()));
                                    }
                                    if (document.getData().get(DAO.dbYear)!=null&&!document.getData().get(DAO.dbYear).toString().equals("")) {
                                        age.add(Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(document.getData().get(DAO.dbYear).toString()));
                                    }
                                    if (document.getData().get(DAO.dbGender)!=null&&document.getData().get(DAO.dbGender).toString().equals(DAO.dbMale)) {
                                        male++;
                                    }
                                    if (document.getData().get(DAO.dbSteps)!=null&&document.getData().get(DAO.dbGender).toString().equals(DAO.dbFemale)) {
                                        female++;
                                    }
                                }

                            }
                            for(int i = 0; i<weight.size();i++){
                                averageWeight = averageWeight + weight.get(i)/weight.size();
                            }
                            for(int i = 0; i<height.size();i++){
                                averageHeight = averageHeight + height.get(i)/height.size();
                            }
                            for(int i = 0; i<steps.size();i++){
                                averageSteps = averageSteps + steps.get(i)/steps.size();
                            }
                            for(int i = 0; i<age.size();i++){
                                averageAge = averageAge + age.get(i)/age.size();
                            }
                            bmi_calc = Float.parseFloat(Integer.toString(averageWeight)) * (10000) / (Float.parseFloat(Integer.toString(averageHeight)) * Float.parseFloat(Integer.toString(averageHeight)));
                            bmi_calc = Math.round(bmi_calc * 100) / 100.0;
                            if(people>=1) {
                                avgWeight.setText(Integer.toString(averageWeight) + " kg");
                                avgHeight.setText(Integer.toString(averageHeight) + " cm");
                                avgBMI.setText(Double.toString(bmi_calc));
                                avgAge.setText(Integer.toString(averageAge));
                                avgSteps.setText(Integer.toString(averageSteps));
                                if ((male + female) != 0) {
                                    mFRate.setText(Integer.toString(100 * male / (male + female)) + " % " + Integer.toString(100 * female / (male + female)) + " %");
                                } else {
                                    mFRate.setText("0 % 0 %");
                                }
                            }


                        } else {
                            Log.d(TAG, "Error getting users!\n", task.getException());
                            Toast.makeText(getActivity(), "Error getting users!\n" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }


    }



}
