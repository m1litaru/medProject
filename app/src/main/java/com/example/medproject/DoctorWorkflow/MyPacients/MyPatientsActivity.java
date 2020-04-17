package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MyPatientsActivity extends AppCompatActivity {

    private static RecyclerView rvList;
    private static TextView emptyView;
    private static PatientAdapter PATIENT_ADAPTER;
    private static DoctorAdapter DOCTOR_ADAPTER;
    private boolean loggedAsDoctor;
    //private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_patients);

//        logged as a patient => loggedAsDoctor = false;
        loggedAsDoctor = false;

        rvList = findViewById(R.id.rvList);

        if(loggedAsDoctor) {
            PATIENT_ADAPTER = new PatientAdapter();
//            loggedAsDoctor needs Patient Adapter because the doctor sees his list of patients
            rvList.setAdapter(PATIENT_ADAPTER);
            initializePage("Pacienții mei", View.VISIBLE);
        } else {
            DOCTOR_ADAPTER = new DoctorAdapter();
            rvList.setAdapter(DOCTOR_ADAPTER);
            initializePage("Doctorii mei", View.GONE);
        }
        //progressBar = findViewById(R.id.progressBar);
    }

    private void initializePage(String title, int showAddPatientButton) {
        setTitle(title);

        emptyView = findViewById(R.id.empty_view);

        LinearLayoutManager listLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(listLayoutManager);

        displayMessageOrList(loggedAsDoctor);

        Button addPatientToDoctor = findViewById(R.id.addPatientToDoctorButton);
        addPatientToDoctor.setVisibility(showAddPatientButton);
        if(showAddPatientButton == View.VISIBLE) {
            addPatientToDoctor.setEnabled(true);
            addPatientToDoctor.setOnClickListener(v -> goToAddPatientPage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_activity_menu, menu);
        menu.removeItem(R.id.insert_menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_account:
                startActivity(new Intent(this, DoctorDetails.class));
                break;
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent toLoginPage = new Intent(this, LoginActivity.class);
                toLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                toLoginPage.putExtra("logOut", "logOut");
                startActivity(toLoginPage);
                break;
        }
        return true;
    }

    private void goToAddPatientPage(){
        startActivity(new Intent(this, AddPatientToDoctorActivity.class));
    }

    public static void displayMessageOrList(boolean loggedAsDoctor) {
        boolean listIsEmpty = loggedAsDoctor ? PATIENT_ADAPTER.noPatientsToDisplay : DOCTOR_ADAPTER.noDoctorsToDisplay;
        if(listIsEmpty)
        {
            rvList.setVisibility(View.GONE);
            if(!loggedAsDoctor) {
                emptyView.setText(R.string.no_doctor_to_display);
            }
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            rvList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        //progressBar.setVisibility(View.GONE);
        //disableControllers(false);
    }

//    private void disableControllers(boolean isEnabled){
//        addPatientToDoctor.setEnabled(!isEnabled);
//    }
}
