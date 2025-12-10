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

/**
 * SMSActivity handles requesting SMS permissions and scheduling daily reminder text messages.
 * Users enter a phone number, and the app will automatically send a weight-entry reminder
 * every day at 08:00 using Android's AlarmManager and SmsReceiver.
 *
 * Flow:
 * 1. User enters phone number and presses the button.
 * 2. Permission is checked (SEND_SMS).
 * 3. If permitted, a repeating alarm is created that broadcasts to SmsReceiver.
 * 4. SmsReceiver handles sending the daily SMS.
 */
public class SMSActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private EditText phoneNumberInput;


    /**
     * Initializes the UI, sets up the button listener, and handles permission checks
     * before enabling daily SMS reminders.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        phoneNumberInput = findViewById(R.id.editPhoneNumber);
        Button sendSMSButton = findViewById(R.id.requestSmsPermissionButton);

        // Validate phone number then check SMS permission then enable daily SMS reminders
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

    /**
     * Handles the result of the SEND_SMS permission request.
     * If granted, the user must press the button again to proceed.
     */
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


    /**
     * Schedules a repeating daily SMS reminder at 08:00 using AlarmManager.
     * Also sends an immediate confirmation SMS to the user.
     *
     * @param phoneNumber The phone number that will receive daily reminders.
     */
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

        // Set repeating alarm if alarmManager is available
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }

        // Send initial confirmation SMS
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                phoneNumber,
                null,
                "WeightWare - Daily Notifications Activated!",
                null,
                null);

        // Update UI to show permission access
        TextView permission = findViewById(R.id.permissionStatusText);
        permission.setText(R.string.permission_granted);
        permission.setTextColor(Color.parseColor("#00FF00"));
    }
}
