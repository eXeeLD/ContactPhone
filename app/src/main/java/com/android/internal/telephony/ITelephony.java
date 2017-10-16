package com.android.internal.telephony;

/**
 * Created by ASUS on 11.10.2017.
 */

public interface ITelephony {

    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}