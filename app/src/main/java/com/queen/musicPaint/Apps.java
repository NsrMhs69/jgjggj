package com.queen.musicPaint;

import android.app.Application;
import android.content.Context;

import com.adivery.sdk.Adivery;

public class Apps extends Application {
    public static Context context;
    private AppOpenManager appOpenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        Adivery.configure(this, getString(R.string.appKey));
        appOpenManager = new AppOpenManager(this);
    }
}
