package com.example.cs360app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;


public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        String phoneNumber = intent.getStringExtra("phone");

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, "Daily Reminder - Enter weight into Weight Aware!", null, null);
        } catch (Exception e){
            Toast.makeText(context, "Failed to send Text" + e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
