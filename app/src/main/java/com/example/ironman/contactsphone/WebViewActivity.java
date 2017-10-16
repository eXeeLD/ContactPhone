package com.example.ironman.contactsphone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {
    private WebView  wv;
    private UtilsSharedPreferns utilsSharedPreferns;
    private MyDataBase mdb = null;
    private SQLiteDatabase db = null;
    private static final String DATABASE_NAME = "ImageD.db";
    public static final int DATABASE_VERSION = 1;
    private Cursor c = null;
    int PERMISSION_REQUEST_CONTACT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        utilsSharedPreferns = new UtilsSharedPreferns();
        wv = (WebView) findViewById(R.id.webView);
        mdb = new MyDataBase(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setFocusable(true);
        wv.setFocusableInTouchMode(true);
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setDatabaseEnabled(true);
        wv.getSettings().setAppCacheEnabled(true);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.loadUrl("http://web1.shaket.co.il");
        wv.setWebViewClient(new WebViewClient());
        askForContactPermission();
    }
    @Override
    public void onBackPressed() {
        if( wv.canGoBack()) {
            wv.goBack();
        } else {

        }
    }
    public void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    Cursor cursor1 = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (cursor1.moveToNext()) {
                        String phoneNumber = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Uri uri = ContentUris.withAppendedId(
                                ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));

                        insertinDatabase(id, phoneNumber);
                        ShowUser();

                    }
                    cursor1.close();
                }
            }
        }
        cursor.close();
    }

    public void ShowUser() {
        db = mdb.getReadableDatabase();
        c = db.query("tableContactsPhone", new String[]{"ID", "PHONE"}, null, null, null, null, null);
        while (c.moveToNext()) {
            // Toast.makeText(getApplicationContext(), c.getString(0) + c.getString(1), Toast.LENGTH_SHORT).show();
            Log.d("GetUsers", c.getString(0) + c.getString(1));
        }
    }


    void insertinDatabase(String id, String phone) {
        db = mdb.getWritableDatabase();
        ContentValues Val = new ContentValues();
        Val.put("ID", id);
        Val.put("PHONE", phone);
        Val.put("BLOCKPHONE", phone);
        db.insertWithOnConflict("tableContactsPhone", null, Val, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    public void askForContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);
                }
            } else {
                loadContacts();
            }
        } else {
            loadContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadContacts();
                } else {

                }
                return;
            }

        }
    }
}

