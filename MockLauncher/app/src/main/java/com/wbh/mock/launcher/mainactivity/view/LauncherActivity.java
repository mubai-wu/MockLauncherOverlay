package com.wbh.mock.launcher.mainactivity.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.wbh.mock.launcher.R;
import com.wbh.mock.launcher.mainactivity.presenter.ILauncherPresenter;
import com.wbh.mock.launcher.mainactivity.presenter.LauncherPresenterImpl;
import com.wbh.mock.launcher.widget.LauncherPagerView;

public class LauncherActivity extends AppCompatActivity implements ILauncherView {

    private ILauncherPresenter mILauncherPresenter;
    private LauncherPagerView mSwipeToOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
        setContentView(R.layout.activity_main);
        initial();
    }

    private void initial() {
        initViews();
        initData();
    }

    private void initViews() {
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mILauncherPresenter.onClick(button);
            }
        });
        mSwipeToOverlayView = findViewById(R.id.swipe_to_overlay_view);
        mSwipeToOverlayView.setOnScrollListener(
                new LauncherPagerView.OnLauncherPageScrollListener() {
                    @Override
                    public void startScroll() {
                        mILauncherPresenter.startScroll();
                    }

                    @Override
                    public void onScroll(int x) {
                        mILauncherPresenter.onScroll(mSwipeToOverlayView, x);
                    }

                    @Override
                    public void endScroll() {
                        mILauncherPresenter.endScroll();
                    }
                }
        );
    }

    private void initData() {
        mILauncherPresenter = new LauncherPresenterImpl(this);
        mILauncherPresenter.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showNegativePage(float progress) {
        mSwipeToOverlayView.scrollTo(
                (int) (-mSwipeToOverlayView.getMeasuredWidth() * progress),
                0);
    }
}
