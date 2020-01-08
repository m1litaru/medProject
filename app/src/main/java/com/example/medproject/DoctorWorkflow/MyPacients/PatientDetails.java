package com.example.medproject.DoctorWorkflow.MyPacients;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.DoctorWorkflow.DoctorDetails;
import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.example.medproject.data.model.Patient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class PatientDetails extends AppCompatActivity {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtAddress;
    private DatabaseReference mDatabaseReference;
    private Button saveChangesButton;
    private boolean canEditForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details);

        txtLastname = findViewById(R.id.txtLastname);
        txtFirstname = findViewById(R.id.txtFirstName);
        txtCNP = findViewById(R.id.txtCNP);
        txtBirthDate = findViewById(R.id.txtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);

        Intent intent = getIntent();
        String patientID = intent.getStringExtra("patientID");
        final String loggedUser = FirebaseAuth.getInstance().getUid();

        canEditForm = patientID == null;

        saveChangesButton = findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String prenume = txtFirstname.getText().toString().trim();
                String nume = txtLastname.getText().toString().trim();
                String telefon = txtPhone.getText().toString().trim();
                String adresaCabinet = txtAddress.getText().toString().trim();
                String CNP = txtCNP.getText().toString().trim();
                String birthDate = txtBirthDate.getText().toString().trim();
                Patient doctor = new Patient(prenume, nume, birthDate, telefon, adresaCabinet,CNP);
                FirebaseDatabase.getInstance().getReference("Patients")
                        .child(loggedUser)
                        .setValue(doctor).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PatientDetails.this, "Contul a fost editat cu succes", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        });

        if(canEditForm) {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + loggedUser);
        } else {
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + patientID);
            saveChangesButton.setVisibility(View.GONE);
        }

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Patient patient = dataSnapshot.getValue(Patient.class);
                txtLastname.setText(patient.getLastName());
                txtFirstname.setText(patient.getFirstName());
                txtCNP.setText(patient.getCNP());
                txtBirthDate.setText(patient.getBirthDate());
                txtPhone.setText(patient.getPhone());
                txtAddress.setText(patient.getAddress());
                enableEditTexts(canEditForm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableEditTexts(boolean isEnabled){
        txtLastname.setEnabled(isEnabled);
        txtFirstname.setEnabled(isEnabled);
        txtCNP.setEnabled(isEnabled);
        txtBirthDate.setEnabled(isEnabled);
        txtPhone.setEnabled(isEnabled);
        txtAddress.setEnabled(isEnabled);
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
                startActivity(new Intent(this, LoginActivity.class));
                Toast.makeText(getApplicationContext(),"V-ați delogat cu succes",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
