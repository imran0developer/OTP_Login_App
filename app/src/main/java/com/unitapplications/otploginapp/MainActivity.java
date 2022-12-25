package com.unitapplications.otploginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    EditText number_et;
    Button sendSMS;
    String new_otp;
    String otp;
    SharedPreferences sharedPref;
    private final static int REQUEST_SEND_SMS_PERMISSION = 1000;
    public final static String SHARED_PREF = "shared_pref";
    public final static String VERIFIED = "verified";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref= getSharedPreferences(SHARED_PREF,MODE_PRIVATE);
        isVerify(); // to verify that the user is verified or not
        //if verified then direct to dashboard activity

        sendSMS = findViewById(R.id.send_bt);
        number_et = findViewById(R.id.number_et);

        isVerify();

        findViewById(R.id.imageView).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this,Verify_Otp.class));
        });

        boolean sent = sharedPref.getBoolean("sent",false);
        if (sent){
            startActivity(new Intent(MainActivity.this,Verify_Otp.class));
        }

        askPermission(); // check the sms send permission


        sendSMS.setOnClickListener(view -> {
            String number = number_et.getText().toString();
            otp = getOtp(); // get one otp from otp list class
            Log.d("otp",number+"Your Login OTP is "+otp);
            if (number.equals("")){ //validating the number is empty or not
                Toast.makeText(this, "Enter your number please", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    sendSMS(number,"Your Login OTP is "+otp); //this method sends the sms
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("sent",true);
                    editor.apply();
                    //Toast.makeText(this, number +"Your Login OTP is "+otp, Toast.LENGTH_SHORT).show();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }
    public void isVerify(){
        boolean verified = sharedPref.getBoolean(VERIFIED,false);
        if (verified){
            startActivity(new Intent(MainActivity.this,Dashboard.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isVerify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVerify();
    }

    public String getOtp(){
        List<String > all_otp = OtpList.AllOtp();
        Random random = new Random();
        int random_num = random.nextInt(all_otp.size());
         new_otp= all_otp.get(random_num);
        return new_otp;
    }
    public void askPermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SEND_SMS_PERMISSION);
        }
    }

    private void sendSMS(String no,String msg) {
        //Getting intent and PendingIntent instance
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 1, intent,0);

        //Get the SmsManager instance and call the sendTextMessage method to send message
        SmsManager sms=SmsManager.getDefault();
        sms.sendTextMessage(no,null, msg, pi,null);

//        Toast.makeText(getApplicationContext(), "OTP request sent successfully!",
//                Toast.LENGTH_LONG).show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.i("SMS", "REQUEST_SEND_SMS_PERMISSION Permission Granted ");
            } else {
                // Permission denied
                Log.i("SMS", "REQUEST_SEND_SMS_PERMISSION Permission Denied");

            }

        }
    }

}