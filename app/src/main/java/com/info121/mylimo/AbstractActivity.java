package com.info121.mylimo;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by KZHTUN on 3/8/2016.
 */
public class AbstractActivity extends AppCompatActivity {
    final String TAG = AbstractActivity.class.getSimpleName();

    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // event bus register
        EventBus.getDefault().register(this);
        Log.e(TAG, "EventBus Registered on Activity ... " + this.getLocalClassName());

    }

    @Override
    protected void onStop() {

        // event bus unregister
        EventBus.getDefault().unregister(this);
        Log.e(TAG, "EventBus Un-Registered on Activity ... " + this.getLocalClassName());
        super.onStop();
    }
}
