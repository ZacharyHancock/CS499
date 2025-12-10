package com.example.cs360app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;


/**
 * SmsReceiver responsible for sending automated SMS messages.
 *
 * <p>This receiver is triggered by an AlarmManager or scheduled intent
 * inside the application. When activated, it retrieves a phone number
 * from the received Intent and sends a reminder SMS encouraging the user
 * to enter their daily weight.</p>
 *
 * <p>If the SMS fails to send, a Toast message displays the error to
 * provide user feedback during debugging or manual testing.</p>
 */
public class SmsReceiver extends BroadcastReceiver {

    /**
     * Called automatically when an associated broadcast is received.
     *
     * @param context the Context in which the receiver is running
     * @param intent  the broadcast Intent containing the phone number
     */
    @Override
    public void onReceive(Context context, Intent intent){
        //gets phone #
        String phoneNumber = intent.getStringExtra("phone");

        // tries to sent text
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Daily Reminder - Enter weight into Weight Aware!", null, null);
        } catch (Exception e){
            Toast.makeText(context, "Failed to send Text" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
