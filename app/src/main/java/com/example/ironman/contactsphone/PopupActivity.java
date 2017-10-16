package com.example.ironman.contactsphone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.BlockedNumberContract;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public class PopupActivity extends AppCompatActivity {
    private RelativeLayout mRelativeLayout;
    private PopupWindow mPopupWindow;
    private Button acessButton;
    private Button closeButton;
    private EditText editPhone;
    private UtilsSharedPreferns utilsSharedPreferns;
    private Retrofit retrofit;
    private static UmoriliApi umoriliApi;

    int PERMISSION_REQUEST_CONTACT = 1;
    private MyDataBase mdb = null;
    private SQLiteDatabase db = null;
    private String baseUrl = "http://web1.shaket.co.il";
    private static final String DATABASE_NAME = "ImageD.db";
    public static final int DATABASE_VERSION = 1;
    private Cursor c = null;
    private WebView wv;
    String numberPhone ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }
        mRelativeLayout = (RelativeLayout) findViewById(R.id.idRev);
        wv = (WebView) findViewById(R.id.webView);
        mdb = new MyDataBase(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        if (isNetworkConnected()) {
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
        } else {
            dialogConnection();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isNetworkConnected()) {
            utilsSharedPreferns = new UtilsSharedPreferns();
            if (utilsSharedPreferns.isActive(PopupActivity.this)) {
                utilsSharedPreferns.setActive(PopupActivity.this, true);

            } else {
                poppupInit();
            }
        }
    }

    public void dialogConnection() {
        Button button;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.retry, null);
        button = (Button) customView.findViewById(R.id.retry);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        PopupActivity.this,
                        PopupActivity.class);
                finish();
                startActivity(intent);
            }
        });
        final AlertDialog dialog = new AlertDialog.Builder(PopupActivity.this)
                .setTitle("Error Connection")
                .setView(customView)
                .setCancelable(false)
                .setMessage("Sorry no connection with Wifi internet, verify your connection ")
                .show();
        dialog.show();
    }

    public static UmoriliApi getApi() {
        return umoriliApi;
    }

    public void poppupInit() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup_layout, null);
        final AlertDialog dialog = new AlertDialog.Builder(PopupActivity.this)
                .setTitle("licens")
                .setMessage("Do you ")
                .setView(customView)
                .setCancelable(false)
                .show();


        acessButton = (Button) customView.findViewById(R.id.acessButton);
        closeButton = (Button) customView.findViewById(R.id.closeButton);
        editPhone = (EditText) customView.findViewById(R.id.phoneEditText);
        acessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utilsSharedPreferns.setActive(PopupActivity.this, true);
                numberPhone = editPhone.getText().toString();
                askForContactPermission();
                dialog.dismiss();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                utilsSharedPreferns.setActive(PopupActivity.this, false);
                finish();

            }
        });


        dialog.show();
    }

    public interface UmoriliApi {
        @FormUrlEncoded
        @POST("/contact.php")
        Call<PostModel> getData(@Field("number") String body,@Field("client_number") String client);
    }

    @Override
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            finish();
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
                        Gson gson = new GsonBuilder()
                                .setLenient()
                                .create();
                        utilsSharedPreferns.setCount(PopupActivity.this,cursor.getCount());
                        retrofit = new Retrofit.Builder()
                                .baseUrl("http://web1.shaket.co.il")
                                .addConverterFactory(GsonConverterFactory.create(gson))
                                .build();
                        umoriliApi = retrofit.create(UmoriliApi.class);
                        utilsSharedPreferns.setPhoneNumber(this,numberPhone);
                       // sendBroadcast(new Intent(PopupActivity.this, PhoneCallReceiver.class));

                        startService(new Intent(PopupActivity.this,ContactObserverService.class));

                        getApi().getData(phoneNumber,numberPhone).enqueue(new Callback<PostModel>() {
                            @Override
                            public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                            }

                            @Override
                            public void onFailure(Call<PostModel> call, Throwable t) {
                            }
                        });
                        // insertinDatabase(id, phoneNumber);


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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

    @Override
    protected void onResume() {
        super.onResume();
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
