package com.example.medproject.DrugInteraction;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medproject.GeneralActivities.BasicActions;
import com.example.medproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DrugInteraction extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText drug1, drug2, interactionText, effectText;
    private TextInputLayout interactionLayout, effectLayout;
    private String drug1Name, drug2Name;
    private boolean searchAgain = false;
    private Button searchInteractionButton;
    private TextView displayDrunkBankInfo;
    private LinearLayout displayInteraction;
    private BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_interaction);
        setTitle("Studiază interacțiunile");

        BasicActions.hideKeyboardWithClick(findViewById(R.id.container), this);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BasicActions.manageNavigationView(this, bottomNavigationView, true);

        drug1 = findViewById(R.id.drug1);
        drug2 = findViewById(R.id.drug2);
        searchInteractionButton = findViewById(R.id.searchInteraction);
        searchInteractionButton.setOnClickListener(this);
        displayDrunkBankInfo = findViewById(R.id.displayDrunkBankInfo);
        displayInteraction = findViewById(R.id.displayInteraction);

        interactionLayout = findViewById(R.id.interactionLayout);
        interactionText = findViewById(R.id.interactionText);
        effectLayout = findViewById(R.id.effectLayout);
        effectText = findViewById(R.id.effectText);

    }

    @Override
    public void onClick(View v) {
        drug1.clearFocus();
        drug2.clearFocus();

        if(searchAgain) {
            drug1.setText("");
            drug2.setText("");
            findViewById(R.id.searchDrugs).setVisibility(View.VISIBLE);
            displayInteraction.setVisibility(View.GONE);
            searchInteractionButton.setText("Caută");
            searchAgain = false;
        }

        drug1Name = drug1.getText().toString();
        drug2Name = drug2.getText().toString();

        BasicActions.manageNavigationView(this, bottomNavigationView, true);
        if(validForm()) {
            searchAgain = true;
            int[] interactions = DrugInteractionHelper.getInteractionFromDatabase(drug1Name, drug2Name);

            new android.os.Handler().postDelayed(() -> {
                    findViewById(R.id.searchDrugs).setVisibility(View.GONE);
                    displayDrunkBankInfo.setVisibility(View.GONE);
                    displayInteraction.setVisibility(View.VISIBLE);
                    searchInteractionButton.setText("Caută din nou");
                    displayInteraction(interactions);
            },1500);
        }
    }

    private void displayInteraction(int[] interactions) {

        ColorStateList color = getColorStateList(DrugInteractionHelper.getInteractionColor(interactions[0]));
        interactionLayout.setEndIconDrawable(DrugInteractionHelper.getInteractionIcon(interactions[0]));
        drug1Name = BasicActions.capitalizeDrugName(drug1Name);
        drug2Name = BasicActions.capitalizeDrugName(drug2Name);
        if(interactions[0] == -1) {
            interactionText.setText(drug1Name + " reduce eficacitatea " + drug2Name + " și invers.");
        } else {
            if(interactions[0] == 1) {
                interactionText.setText(drug1Name + " crește eficacitatea " + drug2Name + " și invers.");
            } else {
                interactionText.setText("Interacțiunea dintre " + drug1Name + " și " + drug2Name + " e necunoscută.");
            }
        }
        interactionLayout.setEndIconTintList(color);
        color = getColorStateList(DrugInteractionHelper.getInteractionColor(interactions[1]));
        effectLayout.setEndIconTintList(color);
        effectLayout.setEndIconDrawable(DrugInteractionHelper.getInteractionIcon(interactions[1]));
        if(interactions[1] == -1) {
            effectText.setText(R.string.effect_text);

        } else {
            effectText.setText("Efectul combinării celor două medicamente asupra corpului uman e necunoscut.");
        }

    }

    private boolean validForm() {

        if(drug1Name.isEmpty()) {
            drug1.setError(getString(R.string.set_drug_name));
            drug1.requestFocus();
            return false;
        }
        if(drug2Name.isEmpty()) {
            drug2.setError(getString(R.string.set_drug_name));
            drug2.requestFocus();
            return false;
        }
        return true;
    }
}
