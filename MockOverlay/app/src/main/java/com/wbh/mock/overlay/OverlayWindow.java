package com.wbh.mock.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.libraries.launcherclient.ILauncherOverlayCallback;
import com.wbh.mock.overlay.view.OverlayDecorView;

public class OverlayWindow {

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
    private OverlayDecorView mOverlayDecorView;
    private int mScreenWidth;
    private boolean mIsWindowAttached = false;
    private float mProgress = 0;
    private ILauncherOverlayCallback mLauncherOverlayCallback;
    private boolean mIsUserSlideOverlay = false;

    public OverlayWindow(Context context) {
        mContext = context;
        mScreenWidth = getScreenWidth();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public void setLayoutParams(WindowManager.LayoutParams layoutParams, ILauncherOverlayCallback cb) {
        mLauncherOverlayCallback = cb;
        if (layoutParams != null) {
            mLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            mLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mLayoutParams.gravity = Gravity.START;
            mLayoutParams.type = layoutParams.type + 1;
            mLayoutParams.token = layoutParams.token;
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            mLayoutParams.x = -mScreenWidth;
            mLayoutParams.format = PixelFormat.TRANSLUCENT;
        }
    }

    public void startScroll() {
        if (mIsWindowAttached) {
            onShowOverlayWindow();
        } else {
            addOverlayWindowToLauncher();
        }
    }

    public void onScroll(float progress) {
        mProgress = progress;
        mOverlayDecorView.scrollTo((int) (mScreenWidth * (1 - progress)), 0);
    }

    public void endScroll() {
        if (mProgress == 0) {
            onHideOverlayWindow();
        } else if(mProgress == 1) {
            onShowOverlayWindow();
        }
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getRealSize(point);
        return point.x;
    }

    private void onShowOverlayWindow() {
        mLayoutParams.x = 0;
        mWindowManager.updateViewLayout(mOverlayDecorView, mLayoutParams);
    }

    private void onHideOverlayWindow() {
        mLayoutParams.x = -mScreenWidth;
        mWindowManager.updateViewLayout(mOverlayDecorView, mLayoutParams);
    }

    private void addOverlayWindowToLauncher() {
        mOverlayDecorView = new OverlayDecorView(mContext);
        mOverlayDecorView.setOnScrollListener(new OverlayDecorView.OnOverlayScrollListener() {
            @Override
            public void startScroll() {
                mIsUserSlideOverlay = true;
            }

            @Override
            public void onScroll(int x) {
                try {
                    if (mIsUserSlideOverlay) {
                        mLauncherOverlayCallback.overlayScrollChanged((float) x / mOverlayDecorView.getMeasuredWidth());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void endScroll() {
                mIsUserSlideOverlay = false;
                int scrollX = mOverlayDecorView.getScrollX();
                if (scrollX == 0) {
                    onShowOverlayWindow();
                } else if (scrollX == mOverlayDecorView.getMeasuredWidth()) {
                    onHideOverlayWindow();
                }
            }
        });
        mOverlayDecorView.setScrollX(mScreenWidth);
        mLayoutParams.x = 0;
        mWindowManager.addView(mOverlayDecorView, mLayoutParams);
        mIsWindowAttached = true;
    }
}
