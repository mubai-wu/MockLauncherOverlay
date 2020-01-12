package com.wbh.mock.launcher.mainactivity.model;

import android.content.Context;
import android.view.WindowManager;

public interface ILauncherModel {
    void bindService(Context context);
    void startScrollOverlay();
    void showOverlayProgress(float progress);
    void endScrollOverlay();
    void setOverlayWindowParams(WindowManager.LayoutParams layoutParams);
}
