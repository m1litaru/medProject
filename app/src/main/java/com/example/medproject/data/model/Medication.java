package com.example.medproject.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class Medication implements Serializable {
    private String id;
    private String diagnostic;
    private String doctorName;
    private List<Drug> drugList;

    public Medication(){}

    public Medication(String diagnostic, String doctorName) {
        this.diagnostic = diagnostic;
        this.doctorName = doctorName;
    }

    public Medication(String id, String diagnostic, String doctorName, List<Drug> drugList) {
        this.id = id;
        this.diagnostic = diagnostic;
        this.doctorName = doctorName;
        this.drugList = drugList;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Medication)
            return ((Medication) obj).id.equals(this.id);
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<Drug> getDrugList() {
        return drugList;
    }

    public void setDrugList(List<Drug> drugList) {
        this.drugList = drugList;
    }
}
