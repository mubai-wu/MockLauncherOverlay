package com.wbh.mock.overlay.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.wbh.mock.overlay.R;

public class OverlayDecorView extends FrameLayout {

    private int mDownX;
    private int mLastX;
    private int mLastY;

    private int mTouchSlop;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private boolean mIsStartScroll;
    private boolean mIsEndScroll;
    private OnOverlayScrollListener mListener;

    public OverlayDecorView(@NonNull Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.overlay_view, this);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
    }

    public void setOnScrollListener(OnOverlayScrollListener listener) {
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
        mIsStartScroll = false;
        mIsEndScroll = false;
        mDownX = downX;
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
        }
    }

    private void handleActionMove(int moveX, int moveY) {
        if (isSlideToLauncher(moveX) && (mDownX - moveX) > mTouchSlop) {
            int dX = moveX - mLastX;
            int dY = moveY - mLastY;
            if (Math.abs(dX) > Math.abs(dY)) {
                if (mListener != null && !mIsStartScroll) {
                    mListener.startScroll();
                    mIsStartScroll = true;
                }
                scrollBy(-dX, 0);
            }
        }
    }

    private void handleActionUp(int upX, int upY) {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVelocity = mVelocityTracker.getXVelocity();
        if (xVelocity < 0) {
            mScroller.startScroll(getScrollX(), 0, getMeasuredWidth() - getScrollX(), 0, 200);
        } else {
            mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 200);
        }
        invalidate();
        mIsEndScroll = true;
    }

    private boolean isSlideToLauncher(int x) {
        return x<= mDownX;
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
    }

    public interface OnOverlayScrollListener {
        void startScroll();
        void onScroll(int x);
        void endScroll();
    }
}
