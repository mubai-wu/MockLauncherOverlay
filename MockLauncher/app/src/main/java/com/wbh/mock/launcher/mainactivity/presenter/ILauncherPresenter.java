package com.wbh.mock.launcher.mainactivity.presenter;

import android.app.Activity;
import android.view.View;

public interface ILauncherPresenter {

    // --- call by view
    void onClick(View view);

    void startScroll();
    void onScroll(View view, int scrollX);
    void endScroll();

    void onCreate(Activity activity);

    // --- call by model

    void onOverlayScroll(float progress);
}
