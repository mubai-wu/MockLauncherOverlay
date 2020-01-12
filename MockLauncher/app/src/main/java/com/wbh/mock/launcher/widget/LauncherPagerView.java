package com.wbh.mock.launcher.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class LauncherPagerView extends FrameLayout {

    private int mDownX;
    private int mLastX;
    private int mLastY;

    private int mTouchSlop;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private boolean mIsStartScroll;
    private boolean mIsEndScroll;
    private OnLauncherPageScrollListener mListener;

    private boolean mHasNegativePage = true;
    // 这里的页数不包涵负一屏
    private int mPageCount = 1;
    // -1 表示负一屏
    private int mCurPageIndex = 0;

    public LauncherPagerView(@NonNull Context context) {
        super(context);
        init();
    }

    public LauncherPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LauncherPagerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
    }

    public void setOnScrollListener(OnLauncherPageScrollListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVelocityTracker.addMovement(event);
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                handleActionUp(x, y);
                break;
            default:
                mIsEndScroll = true;
                break;
        }
        mLastX = x;
        mLastY = y;
        return true;
    }

    private void handleActionDown(int downX, int downY) {
        Log.e("wbh", "handleActionDown --> ");
        mIsStartScroll = false;
        mIsEndScroll = false;
        mDownX = downX;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    private void handleActionMove(int moveX, int moveY) {
        int dX = moveX - mLastX;
        int dY = moveY - mLastY;
        if (Math.abs(dX) > Math.abs(dY)) {
            if (Math.abs(moveX - mDownX) > mTouchSlop) {
                if (mListener != null && !mIsStartScroll) {
                    mListener.startScroll();
                    mIsStartScroll = true;
                }

                if (moveX > mDownX) {
                    onSlideToLeftPage(dX);
                } else {
                    onSlideToRightPage(dX);
                }
            }
        }
    }

    private void handleActionUp(int upX, int upY) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        int index = mCurPageIndex;
        if (upX > mDownX && xVelocity > 0 && canSlideToLeftPage()) { // to right page
            index--;
        } else if (upX < mDownX && xVelocity < 0 && canSlideToRightPage()) { // to left page
            index++;
        }
        int dX = index * getMeasuredWidth() - getScrollX();
        mScroller.startScroll(getScrollX(), 0, dX, 0, 200);

        mIsEndScroll = true;
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mIsEndScroll){
            if (mListener != null) {
                mListener.endScroll();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int scrollX = getScrollX();
        super.onLayout(changed, left + scrollX, top, right + scrollX, bottom);
    }
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        if (mListener != null) {
            mListener.onScroll(x);
        }

        if (x % getMeasuredWidth() == 0) {
            mCurPageIndex = x / getMeasuredWidth();
        }
    }

    private void onSlideToRightPage(int dX) {
        if (canSlideToRightPage()) {
            scrollBy(-dX, 0);
        }
    }

    private void onSlideToLeftPage(int dX) {
        if (canSlideToLeftPage()) {
            scrollBy(-dX, 0);
        }
    }

    private boolean canSlideToRightPage() {
        return mCurPageIndex < mPageCount - 1;
    }

    private boolean canSlideToLeftPage() {
        return mCurPageIndex > 0 || (mCurPageIndex == 0 && mHasNegativePage);
    }

    public interface OnLauncherPageScrollListener {
        void startScroll();
        void onScroll(int x);
        void endScroll();
    }
}
