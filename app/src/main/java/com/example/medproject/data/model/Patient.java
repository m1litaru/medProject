package com.example.medproject.data.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Patient implements Serializable {
    private String id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String phone;
    private String address;
    private String CNP;

    public Patient(){}

    public Patient(String firstName, String lastName, String birthDate, String phone, String CNP) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.CNP = CNP;
    }

    public Patient(String email, String firstName, String lastName, String birthDate, String phone, String address, String CNP) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.CNP = CNP;
    }

    public Patient(String firstName, String lastName, String birthDate, String phone, String address, String CNP) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.address = address;
        this.CNP = CNP;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof Patient)
            return ((Patient) obj).CNP == this.CNP;
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() { return firstName + " " + lastName; }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCNP() {
        return CNP;
    }

    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

}
