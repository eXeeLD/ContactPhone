package com.example.ironman.contactsphone;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ASUS on 12.10.2017.
 */
public class ContactObserver extends ContentObserver {
    private OnUpdate onUpdate;

    public interface OnUpdate{
        void onUpdate();
    }
    public ContactObserver(OnUpdate onUpdate) {
        super(new Handler());
        this.onUpdate = onUpdate;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        onUpdate.onUpdate();
    }
}
