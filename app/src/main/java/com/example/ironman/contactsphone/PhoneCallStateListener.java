package com.example.ironman.contactsphone;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by ASUS on 10.10.2017.
 */

public class PhoneCallStateListener extends PhoneStateListener {

    private Context context;
    private Retrofit retrofit;
    private static UmoriliApi umoriliApi;
    String number;
    public PhoneCallStateListener(Context context){
        this.context = context;
    }


    @Override
    public void onCallStateChanged(int state, final String incomingNumber) {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);

        switch (state) {

            case TelephonyManager.CALL_STATE_RINGING:

                final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                retrofit = new Retrofit.Builder()
                        .baseUrl("http://web1.shaket.co.il")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                umoriliApi = retrofit.create(UmoriliApi.class);
                getApi().getData(incomingNumber).enqueue(new Callback<Example>() {
                    @Override
                    public void onResponse(Call<Example> call, Response<Example> response) {
                        Toast.makeText(context,response.body().get0(),Toast.LENGTH_LONG).show();
                        try {
                            Class clazz = Class.forName(telephonyManager.getClass().getName());
                            Method method = clazz.getDeclaredMethod("getITelephony");
                            method.setAccessible(true);
                            Object telephonyService = method.invoke(telephonyManager);
                            if (response.body().get0().equals("1")) {
                                Toast.makeText(context, "Yes", Toast.LENGTH_LONG).show();
                                clazz = Class.forName(telephonyService.getClass().getName()); // Get its class
                                Method m2 = clazz.getDeclaredMethod("silenceRinger");
                                method = clazz.getDeclaredMethod("endCall"); // Get the "endCall()" method
                                method.setAccessible(true); // Make it accessible
                                method.invoke(telephonyService);
                                m2.invoke(telephonyService);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(Call<Example> call, Throwable t) {
                        Toast.makeText(context,t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

                break;
            case PhoneStateListener.LISTEN_CALL_STATE:


        }
        super.onCallStateChanged(state, incomingNumber);
    }

    public static UmoriliApi getApi() {
        return umoriliApi;
    }

    public interface UmoriliApi {
        @FormUrlEncoded
        @POST("/block.php")
        Call<Example> getData(@Field("number") String body);
    }

}