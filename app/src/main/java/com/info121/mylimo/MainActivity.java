package com.info121.mylimo;

import android.annotation.TargetApi;
import android.os.Build;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    WebView mWebView;
    SwipeRefreshLayout mSwipeRefresh;
    String mylimo = "http://alexisinfo121.noip.me:85/iops_portal";
    String mmclub = "http://submit.1mmclub.com/";

    private static String currentURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeControls();
        initializeEvents();
    }

    private void initializeControls() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // getSupportActionBar().setIcon(R.mipmap.my_limo);
        getSupportActionBar().setTitle(" MY LIMO ");

        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings webSettings = mWebView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
       // webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        mWebView.setInitialScale(100);

        currentURL = mylimo;

      // loadURL();

        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                mSwipeRefresh.setRefreshing(false);
            }

        });



    }

    private void initializeEvents() {
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(currentURL);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "My Limbo").setIcon(R.mipmap.link)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 1, 0, "1MM Club").setIcon(R.mipmap.link)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case 0:
                currentURL = mylimo;

                loadURL();
                Log.e("KZHTUN", "My Limbo");
                return true;
            case 1:
                currentURL = mmclub;
               // openCustomTab(mmclub);
                loadURL();
                Log.e("KZHTUN", "1MM Club");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadURL() {
        mSwipeRefresh.setRefreshing(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Grant permissions for cam
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                Log.d(TAG, "onPermissionRequest");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        Log.d(TAG, request.getOrigin().toString());
                        if(request.getOrigin().toString().equals("file:///")) {
                            Log.d(TAG, "GRANTED");
                            request.grant(request.getResources());
                        } else {
                            Log.d(TAG, "DENIED");
                            request.deny();
                        }
                    }
                });
            }
        });
        mWebView.loadUrl(currentURL);
    }

}
