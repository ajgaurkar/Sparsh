package com.relecotech.androidsparsh;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by yogesh on 07-Oct-16.
 */
public class MyApplication extends Application {
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        //Test commit for gurukul
    }
}
