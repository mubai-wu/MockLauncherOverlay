package com.wbh.mock.launcher.mainactivity.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;
import com.wbh.mock.launcher.mainactivity.presenter.ILauncherPresenter;

public class LauncherModelImpl implements ILauncherModel {

    private final static String OVERLAY_PACKAGE_NAME = "com.wbh.mock.overlay";
    private final static String OVERLAY_SERVICE_ACTION = "com.wbh.mock.overlay.MockService";

    private ILauncherPresenter mILauncherPresenter;
    private ILauncherOverlay mILauncherOverlay;

    private boolean mIsServiceConnected;
    private boolean mIsOverlayWindowAttached;

    private WindowManager.LayoutParams mLayoutParams;

    public LauncherModelImpl(ILauncherPresenter presenter) {
        mILauncherPresenter = presenter;
    }

    @Override
    public void bindService(Context context) {
        if (mIsServiceConnected) {
            return;
        }
        Intent intent = new Intent(OVERLAY_SERVICE_ACTION);
        intent.setPackage(OVERLAY_PACKAGE_NAME);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mIsServiceConnected = true;
                mILauncherOverlay = ILauncherOverlay.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mIsServiceConnected = false;
            }
        }, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void startScrollOverlay() {
        try {
            attachOverlayWindowIfNeed(mLayoutParams);
            if (mILauncherOverlay != null) {
                mILauncherOverlay.startScroll();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showOverlayProgress(float progress) {
        try {
            if (mILauncherOverlay != null) {
                mILauncherOverlay.onScroll(progress);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void endScrollOverlay() {
        try {
            if (mILauncherOverlay != null) {
                mILauncherOverlay.endScroll();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void setOverlayWindowParams(WindowManager.LayoutParams layoutParams) {
        mLayoutParams = layoutParams;
    }

    private void attachOverlayWindowIfNeed(WindowManager.LayoutParams layoutParams) {
        if (mIsOverlayWindowAttached) {
            return;
        }
        try {
            if (mILauncherOverlay != null && layoutParams != null) {
                mILauncherOverlay.windowAttached(layoutParams, new ILauncherOverlayCallback.Stub() {
                    @Override
                    public void overlayScrollChanged(float progress) {
                        mILauncherPresenter.onOverlayScroll(progress);
                    }

                    @Override
                    public void overlayStatusChanged(int status) {

                    }
                }, 1 | 2 | 4 | 8);
                mIsOverlayWindowAttached = true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
