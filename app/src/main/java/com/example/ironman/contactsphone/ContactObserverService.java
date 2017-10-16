package com.example.ironman.contactsphone;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ContactObserverService extends Service implements ContactObserver.OnUpdate {
    private ContactObserver contactObserver;
    SharedPreferences prefs;
    final HashMap<String, Object> first = new LinkedHashMap<String, Object>();
    NavigableMap<String, Object> second = new TreeMap<>();
    private Retrofit retrofit;
    private static UmoriliApi umoriliApi;
    private UtilsSharedPreferns utilsSharedPreferns;
    PhoneCallStateListener phoneListener = null;


    public ContactObserverService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        utilsSharedPreferns = new UtilsSharedPreferns();
        contactObserver = new ContactObserver(this);
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, contactObserver);
    }

    @Override
    public void onDestroy() {
        if (contactObserver != null) {
            this.getContentResolver().unregisterContentObserver(contactObserver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
// intent.getSerializableExtra("List1");

        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (phoneListener == null)
        {
            TelephonyManager tm = (TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
            phoneListener = new PhoneCallStateListener(this);
            tm.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
        }

        return START_STICKY;
    }

    @Override
    public void onUpdate() {
        String value = "";
        List<String> al = new ArrayList<String>();
        String phoneNumber = "";
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
                        phoneNumber = cursor1.getString(cursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        al.add(phoneNumber);
                    }
                    cursor1.close();
                }
            }
        }
        cursor.close();
        if (utilsSharedPreferns.getCount(this) < al.size()) {
            if (al != null && !al.isEmpty()) {
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                retrofit = new Retrofit.Builder()
                        .baseUrl("http://web1.shaket.co.il")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                umoriliApi = retrofit.create(UmoriliApi.class);
                utilsSharedPreferns.setCount(this, al.size());
                Toast.makeText(this, "Number added:" + al.get(al.size() - 1), Toast.LENGTH_SHORT).show();
                getApi().getData(al.get(al.size() - 1),utilsSharedPreferns.getPhone(this)).enqueue(new Callback<PostModel>() {
                    @Override
                    public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                    }

                    @Override
                    public void onFailure(Call<PostModel> call, Throwable t) {
                    }
                });
            }
        }
        else if(utilsSharedPreferns.getCount(this) > al.size()){
            utilsSharedPreferns.setCount(this, al.size());
        }
    }


    public static UmoriliApi getApi() {
        return umoriliApi;
    }

    public interface UmoriliApi {
        @FormUrlEncoded
        @POST("/contact.php")
        Call<PostModel> getData(@Field("number") String body,@Field("client_number") String client);
    }


}