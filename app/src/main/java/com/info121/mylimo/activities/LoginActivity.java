package com.info121.mylimo.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationBuilder;
import com.github.florent37.viewanimator.ViewAnimator;
import com.info121.mylimo.AbstractActivity;
import com.info121.mylimo.App;
import com.info121.mylimo.R;
import com.info121.mylimo.api.APIClient;
import com.info121.mylimo.models.LoginRes;
import com.info121.mylimo.models.UpdateDriverDetailRes;
import com.info121.mylimo.models.VersionRes;
import com.info121.mylimo.services.ShowDialogService;
import com.info121.mylimo.services.SmartLocationService;
import com.info121.mylimo.utils.PrefDB;
import com.info121.mylimo.utils.Util;

import org.greenrobot.eventbus.Subscribe;


public class LoginActivity extends AbstractActivity {
//    private static final int LOCATION_PERMISSION_ID = 1001;
//    private static final int SEND_SMS_PERMISSION_ID = 1002;
//    private static final int CALL_PHONE_PERMISSION_ID = 1003;
//    private static final int CAMERA_PERMISSION_ID = 1004;
//    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_ID = 1005;



    public static String CONST_NOTIFICATION_TONE = "NOTIFICATION_TONE";
    public static String CONST_PROMINENT_TONE = "PROMINENT_TONE";


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE};


    private static final String TAG = LoginActivity.class.getSimpleName();

    String mylimo = "http://alexisinfo121.noip.me:85/iops_portal";
    String mmclub = "http://submit.1mmclub.com/";

    Button mLogin;
    EditText mUserName;
    TextView mUiVersion, mApiVersion;

    PrefDB prefDB = null;
    ProgressBar mProgressBar;

    Context mContext;

    ImageView mBackground, mLogo;
    LinearLayout mLoginLayout;
    CheckBox mRemember;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }



        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        // Get permissions
//        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_ID);
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_ID);
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_ID);
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_ID);
//            return;
//        }

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //  APIClient.GetProduct();

        initializeControls();
        initializeEvents();
        animation();

        // Check and Redirect to Job List

        if (isGPSEnabled()) {
            if (!Util.isNullOrEmpty(App.userName))
                startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
        }

      APIClient.CheckVersion(String.valueOf(Util.getVersionCode(mContext)));

        //  startActivity(new Intent(LoginActivity.this, ToneSelection.class));

        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));

    }

    private void NotificationTest() {
        Intent intent = new Intent(this, ShowDialogService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startService(intent);
    }

    private void initializeControls() {
        mLogin =  findViewById(R.id.login);
        mUserName =  findViewById(R.id.user_name);
        mUiVersion =  findViewById(R.id.ui_version);
        mApiVersion =  findViewById(R.id.api_version);
        mProgressBar =  findViewById(R.id.progressBar);

        mBackground =  findViewById(R.id.image_background);
        mLogo = findViewById(R.id.image_logo);
        mLoginLayout =  findViewById(R.id.login_layout);

        mRemember = findViewById(R.id.remember_me);

        mContext = LoginActivity.this;

        prefDB = new PrefDB(getApplicationContext());


      //  Log.e("Noti :" , App.getNotificationTone().toString());


        if (prefDB.getBoolean(App.CONST_REMEMBER_ME)) {
            mUserName.setText(prefDB.getString(App.CONST_USER_NAME));
            mRemember.setChecked(true);
        }

        mApiVersion.setText("Api " + Util.getVersionCode(mContext));
        mUiVersion.setText("Ver " + Util.getVersionName(mContext));


        if (prefDB.getBoolean(App.CONST_ALREADY_LOGIN) && prefDB.getString(App.CONST_USER_NAME).length() > 0 )
            if (isGPSEnabled()) {
                startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
            }

    }

    private void animation() {
        AnimationBuilder builder;
        //final ViewGroup viewGroup = (ViewGroup)  getParent();

        ImageView imageView;

        imageView = mBackground;
        // ViewAnimator.animate(imageView).wave().duration(2000).start();


//        imageView = mLoginLayout;
        ViewAnimator.animate(mLogo).pulse().interpolator(new BounceInterpolator()).start();
//        ViewAnimator.animate(mLogo).bounceIn();


    }

    private void initializeEvents() {

        Log.e("Login Click", "");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);

                if (mUserName.getText().length() > 0) {
                    APIClient.GetAuthentication(mUserName.getText().toString());
                } else {
                    mUserName.setError("User name should not be blank.");
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });


//        mLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String uri = "https://waze.com/ul?q=pyi" +
//                        "&navigate=yes";
//
//                startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
//            }
//        });


//
//        mLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UploadTask uploadTask = new UploadTask();
//                uploadTask.execute();
//            }
//        });

    }

    @Subscribe
    public void onEvent(LoginRes res) {
        if (res.getValidatedriverResult().getValid().equalsIgnoreCase("Valid")) {

            // Add to Appication Varialbles
            App.userName = getUserName(mUserName.getText().toString());
            App.deviceID = Util.getDeviceID(getApplicationContext());
            App.timerDelay = Long.valueOf(res.getValidatedriverResult().getDuration());

            prefDB.putString(App.CONST_USER_NAME, App.userName);
            prefDB.putString(App.CONST_DEVICE_ID, App.deviceID);
            prefDB.putLong(App.CONST_TIMER_DELAY, App.timerDelay);


            LoginSuccessful();

            if (mRemember.isChecked())
                prefDB.putBoolean(App.CONST_REMEMBER_ME, true);
            else
                prefDB.putBoolean(App.CONST_REMEMBER_ME, false);


        } else {
            mUserName.setError("Wrong user name");
            mProgressBar.setVisibility(View.GONE);
        }

    }


    private void LoginSuccessful() {
        // For Driver Only
        if (!mUserName.getText().toString().contains("~")) {
            APIClient.UpdateDriverDetail(mUserName.getText().toString(), Util.getDeviceID(getApplicationContext()));
            // Start Locaiton Service
            startLocationService();
           // startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
            mProgressBar.setVisibility(View.GONE);
        } else { // Ghost User
            if (isGPSEnabled()) {
                startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }


    private String getUserName(String userName) {
        if (userName.contains("~")) {
            return userName.split("~")[0];
        } else
            return userName;
    }


    @Subscribe
    public void onEvent(UpdateDriverDetailRes res) {
        if (res.getUpdatedeviceResult().equalsIgnoreCase("Success")) {

            if (isGPSEnabled()) {
                startActivity(new Intent(LoginActivity.this, WebViewActivity.class));
                mProgressBar.setVisibility(View.GONE);
            }

        }
        Log.e(TAG, res.getUpdatedeviceResult().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            // startLocationService();
//            finish();
//            startActivity(getIntent());
//        }
//
//        if (requestCode == SEND_SMS_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
//
//        if (requestCode == CALL_PHONE_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
//
//        if (requestCode == CAMERA_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
//
//        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            finish();
//            startActivity(getIntent());
//        }
    }

    private void startLocationService() {
        if (isGPSEnabled()) {
            Intent serviceIntent = new Intent(LoginActivity.this, SmartLocationService.class);
            LoginActivity.this.startService(serviceIntent);
        }
    }


    private boolean isGPSEnabled() {

        mContext = LoginActivity.this;

        final LocationManager manager = (LocationManager) getSystemService(mContext.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            alertDialog.setTitle("GPS Settings");
            alertDialog.setMessage("Your GPS/Location service is off. \n Do you want to turn on location service?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();

            return false;
        } else
            return true;
    }

    @Subscribe
    public void onEvent(Throwable t) {
        mProgressBar.setVisibility(View.GONE);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("Oops!");
        alertDialog.setMessage("Network connection issue. Please try again.");

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Subscribe
    public void onEvent(VersionRes res) {
        if (res.getGetversionResult().equalsIgnoreCase("Outdated")) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.AppName)
                .setMessage(R.string.message_version_outdated)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishAffinity();
                    }
                })
                .setNegativeButton("Go to Play Store", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                })
                .create();

        dialog.show();
    }
}
