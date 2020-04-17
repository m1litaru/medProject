package com.example.medproject;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.medproject.data.model.Doctor;
import com.example.medproject.data.model.Drug;
import com.example.medproject.data.model.Medication;
import com.example.medproject.data.model.MedicationLink;
import com.example.medproject.data.model.Patient;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 123;
    private static ListActivity caller;
    private static boolean isAdmin;
    public static ArrayList<Drug> mDrugs;
    public static ArrayList<Doctor> mDoctors;
    public static ArrayList<Patient> mPatients;
    public static ArrayList<Medication> mMedications;
    public static ArrayList<MedicationLink> mMedicationLink;

    private FirebaseUtil(){}

    public static void openFbReference(String ref, final ListActivity callerActivity){
        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;
            mAuthListener = firebaseAuth -> {
                if(firebaseAuth.getCurrentUser() == null) {
                    FirebaseUtil.signIn();
                }
                else{
                    String userId = firebaseAuth.getUid();
                    checkAdmin(userId);
                }
                Toast.makeText(callerActivity.getBaseContext(),"Bine ați revenit!",Toast.LENGTH_LONG).show();
            };
        }

        mDrugs = new ArrayList<>();
        mDoctors = new ArrayList<>();
        mPatients = new ArrayList<>();
        mMedications = new ArrayList<>();
        mMedicationLink = new ArrayList<>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private static void checkAdmin(String uid){
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("Administrators")
                .child(uid);
        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                caller.showMenu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener);
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }
}
