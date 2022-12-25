package com.unitapplications.otploginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Verify_Otp extends AppCompatActivity {
    EditText otp1,otp2,otp3,otp4;
    Button verify_bt;
    String otp;
    List<String> messages;
    private static final String TAG = "myTag";
    private final static int REQUEST_READ_SMS_PERMISSION = 2000;
    private static final String INBOX_URI = "content://sms/inbox";
    Button sendBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otp1 = findViewById(R.id.otp1);
        verify_bt = findViewById(R.id.verify_bt);

        verify_bt.setOnClickListener(view -> {


            List<String > all_otp = OtpList.AllOtp();
            for (String _otps : all_otp){
                if (_otps.equals(otp)){
                    Toast.makeText(this, "OTP verification successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Verify_Otp.this,Dashboard.class));
                }
            }
        });

        receiveSMS();
    }
    @Override
    protected void onResume() {
        super.onResume();
        receiveSMS();
    }

    private void receiveSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_SMS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        REQUEST_READ_SMS_PERMISSION);
            } else {
                messages  = readSMS();
                otp = msgToOTP(messages.get(0));

                Log.i(TAG, "Messages :: " + messages.get(0));
                if (!otp.equals("")){
                    otp1.setText(otp);
                }
                //Log.i(TAG, "Number of Messages :: " + messages.size());

            }
        }
    }

    private String msgToOTP(String msg) {
        String ref="Your Login OTP is ";
        String otp="0";
        otp= msg.substring(msg.length()-4);
        if (msg.contains(ref)){


             return otp;
        }
        return otp;
    }

    @NonNull
    private List<String> readSMS() {
        Uri mSmsQueryUri = Uri.parse(INBOX_URI);
        List<String> messages = new ArrayList<String>();

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(mSmsQueryUri, null, null, null, null);
            if (cursor == null) {
                Log.i(TAG, "cursor is null. uri: " + mSmsQueryUri);
            }
            for (boolean hasData = cursor.moveToFirst(); hasData; hasData = cursor.moveToNext()) {
                messages.add(cursor.getString(cursor.getColumnIndexOrThrow("body")));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            cursor.close();
        }
        return messages;
    }
}