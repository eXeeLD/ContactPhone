package com.example.ironman.contactsphone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ASUS on 11.10.2017.
 */

class PostModel {

    @SerializedName("number")
        @Expose
        private String number;
    public PostModel(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}


