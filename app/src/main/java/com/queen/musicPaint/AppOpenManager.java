package com.queen.musicPaint;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.adivery.sdk.Adivery;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

public class AppOpenManager implements LifecycleObserver, Application.ActivityLifecycleCallbacks {

    private Activity currentActivity;
    private final Apps myApplication;

    public AppOpenManager(Apps myApplication) {
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(ON_START)
    public void onStart() {
        Adivery.prepareAppOpenAd(currentActivity, Apps.context.getString(R.string.appOpen));
        if (Adivery.isLoaded(Apps.context.getString(R.string.appOpen))){
            Adivery.showAppOpenAd(currentActivity, Apps.context.getString(R.string.appOpen));
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
    }
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }
}