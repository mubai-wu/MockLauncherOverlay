package com.wbh.mock.overlay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wbh.mock.overlay.aidl.Overlay;

public class MockOverlayService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Overlay(getApplicationContext());
    }
}
