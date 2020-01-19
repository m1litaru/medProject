package com.example.medproject.PatientWorkflow.MyDrugs;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medproject.R;
import com.example.medproject.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MyDrugs extends AppCompatActivity implements View.OnClickListener  {
    private FirebaseAuth mAuth;
    private RecyclerView rvDrugs;
    private DrugAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_drugs);

        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Resources res = getResources();
        Drawable syrupBottle = res.getDrawable(R.drawable.medicine_bottle);
        boolean canEditMedicationFlag = intent.getBooleanExtra("canEditMedicationFlag", false);
        adapter = new DrugAdapter(intent.getStringExtra("MedicationID"), canEditMedicationFlag, syrupBottle);
        String diagnostic = intent.getStringExtra("diagnostic");
        setTitle(diagnostic);

        rvDrugs = findViewById(R.id.rvDrugs);
        rvDrugs.setAdapter(adapter);

        LinearLayoutManager drugsLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDrugs.setLayoutManager(drugsLayoutManager);

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
            case R.id.logout_menu:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class).putExtra("logOut", "logOut"));
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}
