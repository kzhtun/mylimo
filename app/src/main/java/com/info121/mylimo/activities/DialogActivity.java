package com.info121.mylimo.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.mylimo.R;
import com.info121.mylimo.api.APIClient;

import java.util.ArrayList;
import java.util.List;

public class DialogActivity extends AppCompatActivity {
    public static final String JOB_NO = "JOB_NO";
    public static final String NAME = "NAME";
    public static final String PHONE = "PHONE";
    public static final String MESSAGE = "MESSAGE";

    Spinner mPhones;
    Button mDismiss, mCall, mConfirm;
    TextView mMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);

        this.setFinishOnTouchOutside(false);


        mMessage = findViewById(R.id.message);
        mPhones = findViewById(R.id.phones);
        mCall = findViewById(R.id.btn_call);
        mConfirm = findViewById(R.id.btn_confirm);
        mDismiss = findViewById(R.id.btn_remind_later);


        // Display Message
        Intent intent = getIntent();

        final String phones = intent.getExtras().getString(PHONE);
        final String message = intent.getExtras().getString(MESSAGE);
        final String jobNo = intent.getExtras().getString(JOB_NO);


//        // for testing purpose only
//        String phones = "09965042219/095137664";
//        String message = "Hi askldfalk adsf asdf adsfj alsfaklsf klasdfj aldsf aldfs afsl";


        // Fill the data
        mMessage.setText(message);
        mPhones.setAdapter(fillPhoneNumbers(phones));


        mCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                phoneCall(mPhones.getSelectedItem().toString());
                APIClient.ConfirmJob(jobNo);
            }
        });

        mDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                APIClient.RemindLater(jobNo);
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                APIClient.ConfirmJob(jobNo);
            }
        });


        // Wake Screen
        PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
        screenLock.acquire();
        screenLock.release();
    }

    public void phoneCall(String phoneNo) {

        Uri number = Uri.parse("tel:" + phoneNo);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(callIntent);
            Toast.makeText(getApplicationContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private ArrayAdapter<String> fillPhoneNumbers(String phones) {
        String p[] = phones.split("/");

        List<String> phoneList = new ArrayList<String>();

        for (String s : p) {
            phoneList.add(s.trim());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, phoneList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }
}
