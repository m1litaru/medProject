package com.example.medproject.Authentication;

import android.content.Intent;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.medproject.AdministratorWorkflow.AddDrug;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.GeneralActivities.MyPatientsOrMyDoctorsActivity;
import com.example.medproject.PatientWorkflow.MyMedications;
import com.example.medproject.R;
import com.example.medproject.Authentication.Register.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private EditText txtEmail;
    private EditText txtPassword;
    private ProgressBar progressBar;
    private Button loginButton, registerButton, forgotPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BasicActions.hideActionBar(this);

        String showLogOut = getIntent().getStringExtra("logOut");
        if(showLogOut != null) {
            BasicActions.displaySnackBar(getWindow().getDecorView(), "V-ați delogat cu succes");
        }

        ConstraintLayout container = findViewById(R.id.container);
        container.setBackgroundResource(R.drawable.icons);

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(container, this);

        mAuth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotPasswordButton = findViewById(R.id.forgotPassword);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);
    }

    private void userLogin(){
        final String email = txtEmail.getText().toString();
        final String password = txtPassword.getText().toString();

        if(authValidation(email, password)){
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                final String userID = mAuth.getCurrentUser().getUid();
                databaseReference.child("Doctors")
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){ //este doctor
                                    finishAffinity();
                                    Intent intent = new Intent(LoginActivity.this, MyPatientsOrMyDoctorsActivity.class);
                                    intent.putExtra("loggedAsDoctor", true);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                                else{ //este pacient sau administrator
                                    databaseReference.child("Patients")
                                            .child(userID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){ //este pacient
                                                        finishAffinity();
                                                        Intent intent = new Intent(LoginActivity.this, MyMedications.class);
                                                        intent.putExtra("loggedAsDoctor", false);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                    else{
                                                        finishAffinity();
                                                        Intent intent = new Intent(LoginActivity.this, AddDrug.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
            else{
                BasicActions.displaySnackBar(getWindow().getDecorView(),"Nu există un cont asociat acestui mail");
            }
            progressBar.setVisibility(View.GONE);
            disableControllers(false);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginButton:
                userLogin();
                break;

            case R.id.registerButton:
                progressBar.setVisibility(View.VISIBLE);
                disableControllers(true);
                Intent intentToRegister = new Intent(this, RegisterActivity.class);
                intentToRegister.putExtra("email", txtEmail.getText().toString());
                intentToRegister.putExtra("password", txtPassword.getText().toString());
                startActivity(intentToRegister);
                break;

            case R.id.forgotPassword:
                progressBar.setVisibility(View.VISIBLE);
                disableControllers(true);
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private boolean authValidation(String email, String password){

        if(email.isEmpty()){
            txtEmail.setError("Introduceți adresa de email");
            txtEmail.requestFocus();
            return true;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Introduceți o adresă validă");
            txtEmail.requestFocus();
            return true;
        }

        if(password.isEmpty()){
            txtPassword.setError("Introduceți parola");
            txtPassword.requestFocus();
            return true;
        }
        if(password.length() < 6){
            txtPassword.setError("Introduceți minim 6 caractere");
            txtPassword.requestFocus();
            return true;
        }

        return false;
    }

    private void disableControllers(boolean isEnabled){
        txtEmail.setEnabled(!isEnabled);
        txtPassword.setEnabled(!isEnabled);
        loginButton.setEnabled(!isEnabled);
        registerButton.setEnabled(!isEnabled);
        forgotPasswordButton.setEnabled(!isEnabled);
    }
}
