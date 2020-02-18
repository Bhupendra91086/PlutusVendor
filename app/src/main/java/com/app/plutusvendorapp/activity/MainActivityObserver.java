package com.app.plutusvendorapp.activity;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

public class MainActivityObserver implements LifecycleObserver {
    private static final String TAG = "MainActivityObserver";

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreateEvent() {
        Log.i(TAG, "Observer ON_CREATE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStartEvent() {
        Log.i(TAG, "Observer ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResumeEvent() {
        Log.i(TAG, "Observer ON_RESUME");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPauseEvent() {
        Log.i(TAG, "Observer ON_PAUSE");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStopEvent() {
        Log.i(TAG, "Observer ON_STOP");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroyEvent() {
        Log.i(TAG, "Observer ON_DESTROY");
    }

}