package com.example.ironman.contactsphone;

import android.net.Uri;

public class Contacts {
    String id;
    String phoneName;
    String phoneNumber;
    Uri photo;


    public Contacts(String phoneName, String phoneNumber,Uri photo) {
        this.phoneName = phoneName;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }

}
