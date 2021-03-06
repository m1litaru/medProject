package com.example.medproject.MyAccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.example.medproject.GeneralActivities.ResourcesHelper;
import com.example.medproject.Models.Patient;
import com.example.medproject.Models.UploadedImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class PatientDetails_Fragment extends Fragment {
    private EditText txtLastname, txtFirstname, txtCNP, txtBirthDate, txtPhone, txtAddress;
    private boolean loggedAsDoctor;
    private ProgressBar progressBar;
    private CircleImageView patientIcon;

    public PatientDetails_Fragment() {// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(getView().findViewById(R.id.container), (AppCompatActivity) getActivity());

        final String loggedUser = FirebaseAuth.getInstance().getUid();

        txtLastname = getView().findViewById(R.id.txtLastname);
        txtFirstname = getView().findViewById(R.id.txtFirstName);
        txtCNP = getView().findViewById(R.id.txtCNP);
        txtBirthDate = getView().findViewById(R.id.txtBirthDate);
        txtPhone = getView().findViewById(R.id.txtPhone);
        txtAddress = getView().findViewById(R.id.txtAddress);
        patientIcon = getView().findViewById(R.id.patientIcon);
        progressBar = getView().findViewById(R.id.progressBar);
        Button saveChangesButton = getView().findViewById(R.id.saveChangesOrDeletePatientButton);
        saveChangesButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            disableControllers(true);

            String firstName = txtFirstname.getText().toString().trim();
            String lastName = txtLastname.getText().toString().trim();
            String phone = txtPhone.getText().toString().trim();
            String address = txtAddress.getText().toString().trim();
            String CNP = txtCNP.getText().toString().trim();
            String birthDate = txtBirthDate.getText().toString().trim();
            Patient patient = new Patient();
            Map<String, Object> detailsToUpdate = patient.setBasicDetails(firstName, lastName, phone, address, CNP, birthDate);
            FirebaseDatabase.getInstance().getReference("Patients")
                    .child(loggedUser)
                    .updateChildren(detailsToUpdate).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    View view = getActivity().getWindow().getDecorView();
                    BasicActions.displaySnackBar(view, "Contul a fost editat cu succes");
                    progressBar.setVisibility(View.GONE);
                    disableControllers(false);
                }
            });

            FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshotDoctors: dataSnapshot.getChildren()) {
                                for (DataSnapshot snapshotPatient: snapshotDoctors.getChildren()) {
                                    if(snapshotPatient.getKey().equals(loggedUser)) {
                                        FirebaseDatabase.getInstance().getReference("DoctorsToPatients")
                                                .child(snapshotDoctors.getKey())
                                                .child(loggedUser)
                                                .child("patient")
                                                .setValue(patient);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        });

        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference("Patients/" + loggedUser);
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
                UploadedImage uploadedImage = patient.getImage();
                if (uploadedImage != null) {
                    Picasso.get()
                            .load(uploadedImage.getImageUrl())
                            .into(patientIcon);
                } else {
                    Picasso.get().
                            load(ResourcesHelper.ICONS.get("defaultUserIconURL")).into(patientIcon);
                }
                disableControllers(loggedAsDoctor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void disableControllers(boolean isEnabled){
        txtLastname.setEnabled(!isEnabled);
        txtFirstname.setEnabled(!isEnabled);
        txtCNP.setEnabled(!isEnabled);
        txtBirthDate.setEnabled(!isEnabled);
        txtPhone.setEnabled(!isEnabled);
        txtAddress.setEnabled(!isEnabled);
    }
}
