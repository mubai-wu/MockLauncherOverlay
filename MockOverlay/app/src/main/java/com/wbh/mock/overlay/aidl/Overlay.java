package com.wbh.mock.overlay.aidl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlay;
import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;
import com.wbh.mock.overlay.OverlayWindow;

import java.lang.ref.WeakReference;

public class Overlay extends ILauncherOverlay.Stub {

    private static final int MSG_WINDOW_ATTACHED = 1;
    private static final int MSG_START_SCROLL = 2;
    private static final int MSG_ON_SCROLL = 3;
    private static final int MSG_END_SCROLL = 4;

    private InternalHandler mHandle;

    private OverlayWindow mOverlayWindow;
    private ILauncherOverlayCallback mLauncherOverlayCallback;

    public Overlay(Context context) {
        mOverlayWindow = new OverlayWindow(context);
        mHandle = new InternalHandler(this);
    }

    @Override
    public void startScroll() throws RemoteException {
        Message msg = Message.obtain();
        msg.what = MSG_START_SCROLL;
        mHandle.sendMessage(msg);
    }

    @Override
    public void onScroll(float progress) throws RemoteException {
        Message msg = Message.obtain();
        msg.what = MSG_ON_SCROLL;
        msg.obj = progress;
        mHandle.sendMessage(msg);
    }

    @Override
    public void endScroll() throws RemoteException {
        Message msg = Message.obtain();
        msg.what = MSG_END_SCROLL;
        mHandle.sendMessage(msg);
    }

    @Override
    public void windowAttached(WindowManager.LayoutParams lp, ILauncherOverlayCallback cb, int flags) throws RemoteException {
        mLauncherOverlayCallback = cb;
        Message msg = Message.obtain();
        msg.what = MSG_WINDOW_ATTACHED;
        Bundle bundle = new Bundle();
        bundle.putParcelable("layout_params", lp);
        bundle.putInt("client_options", flags);
        msg.obj = bundle;
        mHandle.sendMessage(msg);
    }

    @Override
    public void windowDetached(boolean isChangingConfigurations) throws RemoteException {

    }

    @Override
    public void closeOverlay(int flags) throws RemoteException {

    }

    @Override
    public void onPause() throws RemoteException {

    }

    @Override
    public void onResume() throws RemoteException {

    }

    @Override
    public void openOverlay(int flags) throws RemoteException {

    }

    @Override
    public void requestVoiceDetection(boolean start) throws RemoteException {

    }

    @Override
    public String getVoiceSearchLanguage() throws RemoteException {
        return null;
    }

    @Override
    public boolean isVoiceDetectionRunning() throws RemoteException {
        return false;
    }

    @Override
    public boolean hasOverlayContent() throws RemoteException {
        return false;
    }

    @Override
    public void windowAttached2(Bundle bundle, ILauncherOverlayCallback cb) throws RemoteException {

    }

    @Override
    public void unusedMethod() throws RemoteException {

    }

    @Override
    public void setActivityState(int flags) throws RemoteException {

    }

    @Override
    public boolean startSearch(byte[] data, Bundle bundle) throws RemoteException {
        return false;
    }

    private static class InternalHandler extends Handler {

        private WeakReference<Overlay> mWeakReOverlay;

        private InternalHandler(Overlay overlay) {
            mWeakReOverlay = new WeakReference<>(overlay);
        }

        @Override
        public void handleMessage(Message msg) {
            Overlay overlay = mWeakReOverlay.get();
            if (overlay != null) {
                switch (msg.what) {
                    case MSG_WINDOW_ATTACHED:
                        Bundle bundle = (Bundle) msg.obj;
                        WindowManager.LayoutParams lp = bundle.getParcelable("layout_params");
                        overlay.attachedWindow(lp);
                        break;
                    case MSG_START_SCROLL:
                        overlay.onStartScroll();
                        break;
                    case MSG_ON_SCROLL:
                        float progress = (float) msg.obj;
                        overlay.onScrollOnMainThread(progress);
                        break;
                    case MSG_END_SCROLL:
                        overlay.onEndScroll();
                        break;
                }
            }
        }
    }

    private void attachedWindow(WindowManager.LayoutParams lp) {
        mOverlayWindow.setLayoutParams(lp, mLauncherOverlayCallback);
    }

    private void onScrollOnMainThread(float progress) {
        mOverlayWindow.onScroll(progress);
    }

    private void onStartScroll() {
        mOverlayWindow.startScroll();
    }

    private void onEndScroll() {
        mOverlayWindow.endScroll();
    }
}
