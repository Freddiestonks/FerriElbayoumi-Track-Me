package com.trackme.trackme;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String signUpText = "Sign Up";
    private Button signUp;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private TextView message;
    private TextView click;
    private FirebaseAuth mAuth;
    private CheckBox businessType;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        click = findViewById(R.id.Click);
        signUp = findViewById(R.id.signUp);
        signUp.setText(signUpText);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirm);
        businessType = findViewById(R.id.businessSignUP);
        message = findViewById(R.id.messages);
        signButtonUpListener();
        clickListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
        launchActivity(User.class); }
    }

    public void clickListener(){
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(LogInActivity.class);
            }
        });
    }

    public void signButtonUpListener(){
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordSecurityChecker(getPassword(),getConfirmPassword())){
                    createNewUser(getEmail(),getPassword());
                }

            }
        });
    }

    public boolean passwordSecurityChecker(String password,String confirmation){
        if(passwordIsEqual(password,confirmation)){
            if (password.length() > 6){
                return true;
            }
            else {
                message.setText("Passwords must be 6 characters or more");
                message.setTextColor(Color.RED);
                return false;
            }
        }
        else {
            message.setText("Passwords do not match");
            message.setTextColor(Color.RED);
            return false;
        }
    }

    public boolean passwordIsEqual(String password1,String password2){
        return password1.equals(password2);
    }
    private String getPassword(){
        return password.getText().toString();
    }

    private String getConfirmPassword(){
        return confirmPassword.getText().toString();
    }
    private String getEmail(){
        return email.getText().toString();
    }
    private void createNewUser(String inputEmail, String inputPassword){
        mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user!=null) {
                                message.setText("Congratulations, you have registered!");
                                message.setTextColor(Color.GREEN);
                                DBRequestHandler dbRequestHandler = new DBRequestHandler();
                                dbRequestHandler.generateNewDB(businessType.isChecked());
                                launchActivity(User.class);
                            }

                        } else {
                            // If sign in fails, disp
                            //lay a message to the user.
                            message.setText("network error or invalid email");
                            message.setTextColor(Color.RED);

                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }

    private void launchActivity(Class activity) {
        Intent newActivity = new Intent(this, activity);
        startActivity(newActivity);
    }

    public Button getSignUp() {
        return signUp;
    }

}
