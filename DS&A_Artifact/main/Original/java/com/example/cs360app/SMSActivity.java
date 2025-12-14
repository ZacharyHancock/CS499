package com.example.cs360app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.Calendar;

public class SMSActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private EditText phoneNumberInput;

    // initializes the EditText, button, and listener
    // Listener checks to see if phonenumber is valid then checks permissions, then enables the daily SMS spam
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        phoneNumberInput = findViewById(R.id.editPhoneNumber);
        Button sendSMSButton = findViewById(R.id.requestSmsPermissionButton);

        sendSMSButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberInput.getText().toString().trim();

            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
            } else {
                DailySMS(phoneNumber);
            }
        });
    }

    // used to see if the permission code is valid to be able to send messages
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted. Press send again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Sets an alarm to sent a SMS to phone number entered everyday at 0800, as well as sending an initial message that the daily notifications are working, uses the smsreceiver to handle the
    // smsManager notifications being sent out
    private void DailySMS(String phoneNumber){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, SmsReceiver.class);
        intent.putExtra("phone", phoneNumber);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule for 8 AM every day
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "WeightWare - Daily Notifications Activated!", null, null);
        TextView permission = findViewById(R.id.permissionStatusText);
        permission.setText(R.string.permission_granted);
        permission.setTextColor(Color.parseColor("#00FF00"));
    }
}
