package com.example.ironman.contactsphone;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Method;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class CallReceiver extends PhoneCallReceiver {
    String TAG = getClass().getName();
    private static CallReceiver.UmoriliApi umoriliApi;
    @Override
    protected void onIncomingCallReceived(Context ctx, String number, Date start)
    {
        doServerWork(ctx, number);
    }

    private void doServerWork(final Context context, String number){
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://web1.shaket.co.il")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        umoriliApi = retrofit.create(CallReceiver.UmoriliApi.class);
        getApi().getData(number).enqueue(new Callback<Example>() {
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
    }

    public interface UmoriliApi {
        @FormUrlEncoded
        @POST("/block.php")
        Call<Example> getData(@Field("number") String body);
    }

    public static CallReceiver.UmoriliApi getApi() {
        return umoriliApi;
    }


    @Override
    protected void onIncomingCallAnswered(Context ctx, String number, Date start)
    {
        Log.e(TAG, "onIncomingCallReceived: " + number + " onIncomingCallReceived");
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.e(TAG, "onIncomingCallReceived: " + number + " onIncomingCallReceived");
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        Log.e(TAG, "onIncomingCallReceived: " + number + " onIncomingCallReceived");
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        Log.e(TAG, "onIncomingCallReceived: " + number + " onIncomingCallReceived");
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        Log.e(TAG, "onIncomingCallReceived: " + number + " onIncomingCallReceived");
    }

}