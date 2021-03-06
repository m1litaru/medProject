package com.example.medproject.AdministratorWorkflow;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.DoctorWorkflow.AddMedication.AddDrugToMedication;
import com.example.medproject.R;
import com.example.medproject.Authentication.LoginActivity;
import com.example.medproject.Models.Drug;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddDrug extends AppCompatActivity implements View.OnClickListener{

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText txtNume, txtScop, txtUnitate, txtDescriere;
    private final Drug drug = new Drug();
    private boolean goToAddDrugToMedication = false;
    private Button saveButton;
    private ProgressBar progressBar;


    @Override
    protected void onStart() {
        super.onStart();
        BasicActions.checkIfUserIsLogged(this);
        setContentView(R.layout.add_drug);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Drugs");

        progressBar = findViewById(R.id.progressBar);
        // hiding keyboard when the container is clicked
        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);

        Intent intentFromDoctor = getIntent();
        String drugName = intentFromDoctor.getStringExtra("drugName");

        txtNume = findViewById(R.id.txtNume);
        if(drugName != null) {
            goToAddDrugToMedication = true;
            txtNume.setText(drugName);
        }
        txtScop = findViewById(R.id.txtScop);
        txtUnitate = findViewById(R.id.txtUnitate);
        txtDescriere = findViewById(R.id.txtDescriere);
        saveButton = findViewById(R.id.addDrugButton);
        saveButton.setOnClickListener(this);
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
        if (item.getItemId() == R.id.logout_menu) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent toLoginPage = new Intent(this, LoginActivity.class);
            toLoginPage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            toLoginPage.putExtra("logOut", "logOut");
            startActivity(toLoginPage);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        saveDrug();
        clean();
        progressBar.setVisibility(View.GONE);
        disableControllers(false);
    }

    private void saveDrug(){
        progressBar.setVisibility(View.VISIBLE);
        disableControllers(true);

        drug.setNume(txtNume.getText().toString());
        drug.setScop(txtScop.getText().toString());
        drug.setUnitate(txtUnitate.getText().toString());
        drug.setDescriere(txtDescriere.getText().toString());
        DatabaseReference pushedDrugRef = mDatabaseReference.push();
        pushedDrugRef.setValue(drug);
        String drugId = pushedDrugRef.getKey();
        BasicActions.displaySnackBar(getWindow().getDecorView(),"Medicamentul " + drug.getNume() + " a fost salvat");
        if(goToAddDrugToMedication){
            Intent backToMedication = new Intent(this, AddDrugToMedication.class);
            backToMedication.putExtra("drugId", drugId);
            startActivity(backToMedication);
        }
    }

    private void deleteDrug(){
        if(drug == null){
            BasicActions.displaySnackBar(getWindow().getDecorView(),"Vă rugăm salvați medicamentul înainte să îl ștergeți!");
            return;
        }
        mDatabaseReference.child(drug.getId()).removeValue();
    }

    private void clean() {
        txtNume.setText("");
        txtScop.setText("");
        txtUnitate.setText("");
        txtDescriere.setText("");
    }

    private void disableControllers(boolean isEnabled){
        txtNume.setEnabled(!isEnabled);
        txtScop.setEnabled(!isEnabled);
        txtUnitate.setEnabled(!isEnabled);
        txtDescriere.setEnabled(!isEnabled);
        saveButton.setEnabled(!isEnabled);

    }
}
