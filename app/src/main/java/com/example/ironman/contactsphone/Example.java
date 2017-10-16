package com.example.ironman.contactsphone;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ASUS on 13.10.2017.
 */

public class Example {

    @SerializedName("0")
    @Expose
    private String _0;
    @SerializedName("id")
    @Expose
    private String id;

    public String get0() {
        return _0;
    }

    public void set0(String _0) {
        this._0 = _0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}