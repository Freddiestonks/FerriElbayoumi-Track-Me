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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static java.text.DateFormat.getDateInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class FitnessLevel extends Fragment {
    private static final String TAG = "WE";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private DBRequestHandler mDBRequestHandler;
    private EditText weight;
    private EditText height;
    private TextView stepsText;
    private Button mButton;
    private TextView bmi;
    private TextView hint;
    private double bmi_calc = 22;
    private int daily_steps = 0;
    private final static String dbHeight = "Height";
    private final static String dbWeight = "Weight";
    private final static String dbSteps = "Steps";
    private final static String dbBmi = "BMI";

    private TextView fitnessLevel;

    public FitnessLevel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fitness_level, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mDBRequestHandler = new DBRequestHandler();
        weight = view.findViewById(R.id.weight);
        height = view.findViewById(R.id.height);
        mButton = view.findViewById(R.id.update_Fit);
        hint = view.findViewById(R.id.hint);
        bmi = view.findViewById(R.id.BMI);
        stepsText = view.findViewById(R.id.steps);
        fitnessLevel = view.findViewById(R.id.fitness_level);
        daily_steps = getArguments().getInt("steps");
        weight.setText(getArguments().getString(dbWeight));
        height.setText(getArguments().getString(dbHeight));
        fitButtonUpListener();
        filler();
        updateStats();
        updateFitnessLevel();
        // Inflate the layout for this fragment
        return view;
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
                                if(map.get(dbHeight) != null){
                                    height.setText(Objects.requireNonNull(map.get(dbHeight)).toString());
                                }
                                if(map.get(dbWeight) != null){
                                    weight.setText(Objects.requireNonNull(map.get(dbWeight)).toString());
                                }
                                updateStats();
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
//        if(!mDBRequestHandler.getFromDB(mDBRequestHandler.getDbWeight()).toString().equals(""))
//            weight.setText(mDBRequestHandler.getFromDB(mDBRequestHandler.getDbWeight()));

    }
    private void fitButtonUpListener(){
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateStats();
            }
        });
    }

    private void updateFitnessLevel(){
        int score;
        score = daily_steps /100;
        if(bmi_calc>25){
            score = score - (int)Math.round(8*(bmi_calc-25));
        }
        else if(bmi_calc<25 && bmi_calc>18.5){
            score = score + 20;
        }
        fitnessLevel.setText(Integer.toString(score));
        stepsText.setText(Integer.toString(daily_steps));
        fitnessLevel.setTextColor(Color.GREEN);
        stepsText.setTextColor(Color.GREEN);

        if (score<90&&score>=70){
            fitnessLevel.setTextColor(Color.BLUE);
            stepsText.setTextColor(Color.BLUE);

        }
        else if (score<70&&score>=50){
            fitnessLevel.setTextColor(Color.rgb(255, 165, 0));
            stepsText.setTextColor(Color.rgb(255, 165, 0));

        }
        else if (score<50){
            fitnessLevel.setTextColor(Color.RED);
            stepsText.setTextColor(Color.RED);


        }
    }

    private void updateStats(){
        if(!weight.getText().toString().equals("") && !height.getText().toString().equals("")) {
            mDBRequestHandler.updateDB(dbHeight,Integer.parseInt(height.getText().toString()));
            mDBRequestHandler.updateDB(mDBRequestHandler.getDbWeight(),Integer.parseInt(weight.getText().toString()));
            mDBRequestHandler.updateDB(dbSteps,Integer.toString(daily_steps));
            bmi_calc = Float.parseFloat(weight.getText().toString()) * (10000) / (Float.parseFloat(height.getText().toString()) * Float.parseFloat(height.getText().toString()));
            bmi_calc = Math.round(bmi_calc * 100) / 100.0;
            bmi.setText(Double.toString(bmi_calc));
            if (bmi_calc < 18.5) {
                bmi.setTextColor(Color.BLUE);
                hint.setText("Underweight: You are currently underweight, this means that you should take more calories per day than you actually do, moreover remember that you should always keep a good fitness activity");
                hint.setTextColor(Color.BLUE);
            } else if (bmi_calc < 24.9) {
                bmi.setTextColor(Color.GREEN);
                hint.setText("Perfect Weight: Keep up like this, however remember that you should always keep a good fitness activity");
                hint.setTextColor(Color.GREEN);
            } else if (bmi_calc < 29.9) {
                bmi.setTextColor(Color.rgb(255, 165, 0));
                hint.setText("Overweight: You are currently overweight, this means that you are eating more than you currently should and are not taking enough nutrients, switch to an healthy lifestyle by eating more vegetables and by increasing your fitness activity");
                hint.setTextColor(Color.rgb(255, 165, 0));
            } else {
                bmi.setTextColor(Color.RED);
                hint.setText("Obese: You are currently obese!!! this means that you are eating way over what you should and are not taking enough nutrients,stop consuming junk food and start walking outside, our planet is wonderful,consult a physician for mor infos");
                hint.setTextColor(Color.RED);
            }
            mDBRequestHandler.updateDB(dbBmi,Double.toString(bmi_calc));

            updateFitnessLevel();
        }
    }



}
