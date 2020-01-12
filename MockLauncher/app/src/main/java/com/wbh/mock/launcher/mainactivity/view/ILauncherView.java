package com.wbh.mock.launcher.mainactivity.view;

import android.app.Activity;

public interface ILauncherView {
    Activity getActivity();

    void showNegativePage(float progress);
}
