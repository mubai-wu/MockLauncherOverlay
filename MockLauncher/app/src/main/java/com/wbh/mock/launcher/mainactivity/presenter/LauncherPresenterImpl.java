package com.wbh.mock.launcher.mainactivity.presenter;

import android.app.Activity;
import android.view.View;

import com.wbh.mock.launcher.mainactivity.model.ILauncherModel;
import com.wbh.mock.launcher.mainactivity.model.LauncherModelImpl;
import com.wbh.mock.launcher.mainactivity.view.ILauncherView;

public class LauncherPresenterImpl implements ILauncherPresenter {

    private ILauncherView mILauncherView;
    private ILauncherModel mILauncherModel;
    private boolean mStartedSendingScrollEvents;
    private boolean mIsUserSlideLauncher = false;

    public LauncherPresenterImpl(ILauncherView iLauncherView) {
        mILauncherView = iLauncherView;
        mILauncherModel = new LauncherModelImpl(this);
    }

    @Override
    public void onClick(View view) {
        mILauncherModel.bindService(view.getContext());
    }

    @Override
    public void startScroll() {
        mIsUserSlideLauncher = true;
    }

    @Override
    public void onScroll(View view, int scrollX) {
        boolean shouldScrollOverlay = scrollX <= 0;
        if (shouldScrollOverlay && mIsUserSlideLauncher) {
            if (!mStartedSendingScrollEvents) {
                mILauncherModel.startScrollOverlay();
                mStartedSendingScrollEvents = true;
            }
            mILauncherModel.showOverlayProgress(-scrollX * 1.0f / view.getMeasuredWidth());
        }
    }

    @Override
    public void endScroll() {
        mIsUserSlideLauncher = false;
        if (mStartedSendingScrollEvents){
            mILauncherModel.endScrollOverlay();
            mStartedSendingScrollEvents = false;
        }
    }

    @Override
    public void onCreate(Activity activity) {
        mILauncherModel.setOverlayWindowParams(activity.getWindow().getAttributes());
    }

    @Override
    public void onOverlayScroll(float progress) {
        mILauncherView.showNegativePage(1 - progress);
    }

}
