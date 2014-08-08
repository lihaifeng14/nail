package com.nail.core.widget;

import com.nail.core.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public abstract class PullToRefreshBaseView<T extends View> extends LinearLayout {

    public static final int STATE_INIT = 0;
    public static final int STATE_PULL_TO_REFRESH = 1;
    public static final int STATE_RELEASE_TO_REFRESH = 2;
    public static final int STATE_PULL_REFRESHING = 3;
    public static final int STATE_MANUAL_REFRESHING = 4;

    private int mTouchSlop;
    private float mInitialMotionX, mInitialMotionY;
    private float mLastMotionX, mLastMotionY;

    private boolean mIsBeingDragged = false;
    private T mInternalView;
    private View mHeaderLayout;

    private int mState;

    protected abstract T createPullToRefreshView(Context context, AttributeSet attrs);
    protected abstract boolean couldPullToRefresh();

    public PullToRefreshBaseView(Context context) {
        super(context);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
    }

    public final T getInternalView() {
        return mInternalView;
    }

    private void init(Context context, AttributeSet attrs) {
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getTouchSlop();

        mInternalView = createPullToRefreshView(context, attrs);
        // 把Header和内容都加入到LinearLayout中
        addInternalView();
        addHeaderView();
    }

    private void addInternalView() {
        setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mInternalView, param);
    }

    private void addHeaderView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mHeaderLayout = inflater.inflate(R.layout.pulltorefresh_header_layout, null);
        mHeaderLayout.setVisibility(View.GONE);
        addView(mHeaderLayout, 0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            mIsBeingDragged = false;
            return false;
        }
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true;
        }

        switch(action) {
        case MotionEvent.ACTION_DOWN:
            if (couldPullToRefresh()) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (couldPullToRefresh()) {
                float deltaY = mLastMotionY - ev.getY();
                float deltaX = mLastMotionX - ev.getX();
                if (!mIsBeingDragged && deltaY > mTouchSlop && Math.abs(deltaY) > Math.abs(deltaX)) {
                    mLastMotionX = ev.getX();
                    mLastMotionY = ev.getY();
                    mIsBeingDragged = true;
                }
            }
            break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch(action) {
        case MotionEvent.ACTION_DOWN:
            if (couldPullToRefresh()) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mIsBeingDragged = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (couldPullToRefresh()) {
                float deltaY = mLastMotionY - ev.getY();
                float deltaX = mLastMotionX - ev.getX();
                if (!mIsBeingDragged && deltaY > mTouchSlop && Math.abs(deltaY) > Math.abs(deltaX)) {
                    mLastMotionX = ev.getX();
                    mLastMotionY = ev.getY();
                    mIsBeingDragged = true;
                }
                if (mIsBeingDragged) {
                    mLastMotionX = ev.getX();
                    mLastMotionY = ev.getY();
                    doWithPullEvents();
                }
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            break;
        }
        return true;
    }

    private void doWithPullEvents() {
        
    }
}