package com.info121.mylimo.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.info121.mylimo.AbstractActivity;
import com.info121.mylimo.App;
import com.info121.mylimo.R;
import com.info121.mylimo.api.APIClient;
import com.info121.mylimo.models.SaveShowPicRes;
import com.info121.mylimo.models.VersionRes;
import com.info121.mylimo.services.SmartLocationService;
import com.info121.mylimo.utils.FtpHelper;
import com.info121.mylimo.utils.PrefDB;
import com.info121.mylimo.utils.Util;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.InputStream;

import static com.info121.mylimo.utils.FtpHelper.getImageUri;
import static com.info121.mylimo.utils.FtpHelper.getRealPathFromURI;

public class WebViewActivity extends AbstractActivity {
    private static final int REQUEST_CAMERA_IMAGE = 2001;


    WebView mWebView;
    Toolbar mToolbar;
    ProgressBar mProgressBar;
    Context mContext;
    Button mGoogleMap, mWaze, mChangi, mFlightRader, mFlightAware, mNoti, mProminent;

    LinearLayout mAppListContainer;

    String mFileName;
    String mJobNo;

    String url = "";

    PrefDB prefDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        initializeControls();

        // call api to checkVersion
        APIClient.CheckVersion(String.valueOf(Util.getVersionCode(mContext)));

       // openCamera("test.jpg", "001");

    }

    void initializeControls() {

        prefDB = new PrefDB(getApplicationContext());
        App.userName = prefDB.getString(App.CONST_USER_NAME);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Welcome " + App.userName);
        setSupportActionBar(mToolbar);

        mWebView = (WebView) findViewById(R.id.web_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        WebSettings webSettings = mWebView.getSettings();

        mWebView.setVisibility(View.GONE);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.setInitialScale(100);
        mWebView.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        mContext = WebViewActivity.this;


        url = String.format(App.CONST_URL_JOB_LIST, App.userName);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                super.onPageFinished(view, url);
            }
        });

        mWebView.loadUrl(url);

        App.targetContent = WebViewActivity.this;

//        showAlertDialog();
//        //16.8293621,96.1528663
//        showAppSelectionDialog(16.8293621,96.1528663);
    }

    public void showNavAppSelectionDialog(final double lat, final double lng, final String address) {

        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_navigation_app_selection);
        dialog.setTitle("App Selection");


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        mGoogleMap = (Button) dialog.findViewById(R.id.btn_google_map);
        mWaze = (Button) dialog.findViewById(R.id.btn_waze);


        if (!hasGoogleMap())
            mGoogleMap.setVisibility(View.GONE);

        if (!hasWaze())
            mWaze.setVisibility(View.GONE);


        mGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (hasGoogleMap())
                    showRouteOnGoogleMap(lat, lng);
                else
                    gotoPlayStore("com.google.android.apps.maps");


            }
        });

        mWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (hasWaze())
                    showRouteOnWaze(lat, lng, address);
                else
                    gotoPlayStore("com.waze");
            }
        });

        dialog.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public boolean hasGoogleMap() {
        try {
            String gMap = "com.google.android.apps.maps";
            Drawable gMapIcon = mContext.getPackageManager().getApplicationIcon(gMap);

            // Assign icon
          //  mGoogleMap.setBackground(gMapIcon);
            return true;
        } catch (PackageManager.NameNotFoundException ne) {
            return false;
        }
    }

    public boolean hasWaze() {
        try {
            String waze = "com.waze";
            Drawable wazeIcon = mContext.getPackageManager().getApplicationIcon(waze);

            // Assign icon
           // mWaze.setBackground(wazeIcon);
            return true;
        } catch (PackageManager.NameNotFoundException ne) {
            return false;
        }
    }


    public void showRouteOnGoogleMap(double lat, double lng) {
        String uri = "http://maps.google.com/maps?saddr=" +
                App.location.getLatitude() + "," +
                App.location.getLongitude() + "&daddr=" +
                lat + "," + lng;

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    public void showRouteOnWaze(double lat, double lng, String address) {
//        String uri = "waze://?ll=" + lat + "," + lng +
//                "&navigate=yes";

        String uri = "https://waze.com/ul?q=" + address +
                "&navigate=yes";

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.waze");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, "Option")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        showSettingSelectDialog();
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(WebViewActivity.this, R.mipmap.ic_settings_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 0, Menu.NONE, "Refresh")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        mProgressBar.setVisibility(View.VISIBLE);
                        mWebView.loadUrl(url);
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(WebViewActivity.this, R.mipmap.ic_autorenew_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 1, Menu.NONE, "Logout")
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        finish();
                        prefDB.remove(App.CONST_ALREADY_LOGIN);

                        stopLocationService();

                        App.userName = null;
                        return true;
                    }
                })
                .setIcon(ContextCompat.getDrawable(WebViewActivity.this, R.mipmap.ic_exit_to_app_white_24dp))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


//        menu.add(Menu.NONE, 2, Menu.NONE, "Camera")
//                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                        startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
//                        return true;
//                    }
//                })
//                .setIcon(ContextCompat.getDrawable(WebViewActivity.this, R.mipmap.ic_exit_to_app_white_24dp))
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }


    private void stopLocationService(){
        Intent intent = new Intent(WebViewActivity.this, SmartLocationService.class);
        WebViewActivity.this.stopService(intent);
    }

    @Subscribe
    public void onEvent(SaveShowPicRes res) {
        if (res.getSaveshowpicResult().equalsIgnoreCase("Success")) {
            Log.e("Save Show Pic : ", "Success... ");
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_IMAGE && resultCode == RESULT_OK) {
            try {

                Bundle extras = data.getExtras();

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(getRealPathFromURI(getApplicationContext(), tempUri));

                InputStream inputStream = getContentResolver().openInputStream(tempUri);

                FtpHelper.uploadTask async = new FtpHelper.uploadTask(WebViewActivity.this, inputStream);
                async.execute(App.FTP_URL, App.FTP_USER, App.FTP_PASSWORD, App.FTP_DIR, mFileName, mJobNo, "PHOTO");   //Passing arguments to AsyncThread


            } catch (Exception e) {
                Log.e("Camera Error : ", e.getLocalizedMessage().toString());
            }
        }

    }








    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Please do not use this function in My Limo application.");

        // On pressing Settings button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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

    public Drawable getAppIcon(String packageName) {
        try {
            return mContext.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException ne) {
            return null;
        }
    }



    private void launchApp(String appPackageName) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appPackageName);
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        } else
            gotoPlayStore(appPackageName);
    }


    public void gotoPlayStore(String appPackageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }


    private void showFlightAppsDialog() {

        final String package_changi = "com.changiairport.cagapp";
        final String package_flight_rader = "com.flightradar24free";
        final String package_flight_aware = "com.flightaware.android.liveFlightTracker";

        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_flight_app_selection);
        dialog.setTitle("App Selection");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // to cancel when outside touch
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        // window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        mAppListContainer = (LinearLayout) dialog.findViewById(R.id.app_list);
        mChangi = (Button) dialog.findViewById(R.id.btn_ichangi);
        mFlightRader = (Button) dialog.findViewById(R.id.btn_flight_rader);
        mFlightAware = (Button) dialog.findViewById(R.id.btn_flight_aware);


        mChangi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_changi);
                dialog.dismiss();
            }
        });

        mFlightRader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_flight_rader);
                dialog.dismiss();
            }
        });

        mFlightAware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchApp(package_flight_aware);
                dialog.dismiss();
            }
        });



        dialog.show();

    }

    private void showSettingSelectDialog() {

        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat);
        dialog.setContentView(R.layout.dialog_setting_selection);
        dialog.setTitle("App Selection");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Window window = dialog.getWindow();
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);

        //adding dialog animation sliding up and down
        // dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        // to cancel when outside touch
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_Fade;

        // window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        mNoti = (Button) dialog.findViewById(R.id.btn_notification);
        mProminent = (Button) dialog.findViewById(R.id.btn_prominent);


        mProminent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(mContext, ToneSelection.class);
                intent.putExtra(ToneSelection.TONE_TYPE, "PROMINENT");
                startActivity(intent);
            }
        });

        mNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                Intent intent = new Intent(mContext, ToneSelection.class);
                intent.putExtra(ToneSelection.TONE_TYPE, "NOTIFICATION");
                startActivity(intent);
            }
        });


        dialog.show();

    }


    public class JavaScriptInterface {
        private final WebViewActivity webViewActivity;

        JavaScriptInterface(WebViewActivity webViewActivity) {
            this.webViewActivity = webViewActivity;
        }


        @JavascriptInterface
        public void openSignaturePad(String jobNo, String fileName){
            Intent signatureIntent = new Intent(WebViewActivity.this, SignaturePadActivity.class);
            signatureIntent.putExtra("JOBNO", jobNo);
            signatureIntent.putExtra("FILENAME", fileName);

            startActivity(signatureIntent);
        }


        @JavascriptInterface
        public void openCamera(final String fileName, final String jobNo) {

            mFileName = fileName;
            mJobNo = jobNo;

            webViewActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra("filename", fileName);
                    startActivityForResult(intent, REQUEST_CAMERA_IMAGE);
                }
            });
        }

        @JavascriptInterface
        public void phoneCall(String phoneNo) {

            Uri number = Uri.parse("tel:" + phoneNo);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(callIntent);

            Toast.makeText(getApplicationContext(), "Phone Call .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void sendSMS(String phoneNo) {
            String msg = "";
            Uri number = Uri.parse("sms:" + phoneNo);
            Intent smsIntent = new Intent(Intent.ACTION_VIEW, number);
            startActivity(smsIntent);

            Toast.makeText(getApplicationContext(), "Send SMS .... to  " + phoneNo, Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void openFile(String url) {
            Uri uri = Uri.parse(url);

            // create an intent builder
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

            // Begin customizing
            // set toolbar colors
            intentBuilder.setToolbarColor(ContextCompat.getColor(WebViewActivity.this, R.color.colorPrimary));
            intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(WebViewActivity.this, R.color.colorPrimaryDark));

            // set start and exit animations
            //            intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
            //            intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
            //                    android.R.anim.slide_out_right);

            // build custom tabs intent
            CustomTabsIntent customTabsIntent = intentBuilder.build();

            // launch the url
            customTabsIntent.launchUrl(WebViewActivity.this, uri);

        }

        @JavascriptInterface
        public void showRoute(double lat, double lng, String address) {
            showNavAppSelectionDialog(lat, lng, address);
        }

        @JavascriptInterface
        public void showFlight() {
            showFlightAppsDialog();
        }

    }

}



