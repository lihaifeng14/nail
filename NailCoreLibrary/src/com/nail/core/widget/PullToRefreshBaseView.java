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

public abstract class PullToRefreshBaseView<T extends View> extends LinearLayout {

    public static final int STATE_INIT                  = 0;
    public static final int STATE_PULL_TO_REFRESH       = 1;
    public static final int STATE_RELEASE_TO_REFRESH    = 2;
    public static final int STATE_PULL_REFRESHING       = 3;
    public static final int STATE_MANUAL_REFRESHING     = 4;

    private int mTouchSlop;
    private float mInitialMotionX, mInitialMotionY;
    private float mLastMotionX, mLastMotionY;

    private boolean mIsBeingDragged = false;
    private T mInternalView;

    // 两个Header，一个用于下拉，一个用于刷新过程
    private View mPullRefreshLayout;
    private View mRefreshingLayout;
    private boolean mIsRefreshingLayoutInit = false;

    private int mState;
    private RefreshListener mRefreshListener;

    public static interface RefreshListener {
        public void onRefreshStarted();
    }

    protected abstract T createPullToRefreshView(Context context, AttributeSet attrs);
    protected abstract boolean couldPullToRefresh();

    public PullToRefreshBaseView(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init(context, attrs);
    }

    public final T getInternalView() {
        return mInternalView;
    }

    public void setRefreshing(boolean start) {
        
    }

    public void onRefreshComplete() {
        if (isRefreshing()) {
            changeState(STATE_INIT);
        }
    }

    public void setRefreshListener(RefreshListener listener) {
        mRefreshListener = listener;
    }

    public void initRefreshHeader() {
        mIsRefreshingLayoutInit = true;
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
        mPullRefreshLayout = inflater.inflate(R.layout.pulltorefresh_header_layout, null);
        mPullRefreshLayout.setVisibility(View.GONE);
        addView(mPullRefreshLayout, 0);

        mRefreshingLayout = inflater.inflate(R.layout.pulltorefresh_header_layout, null);
        mRefreshingLayout.setVisibility(View.GONE);
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
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (couldPullToRefresh()) {
                float deltaY = mLastMotionY - ev.getY();
                float deltaX = mLastMotionX - ev.getX();
                if (!mIsBeingDragged && deltaY > mTouchSlop && Math.abs(deltaY) > Math.abs(deltaX)) {
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
            if (mIsBeingDragged) {
                mIsBeingDragged = false;
                if (mState == STATE_RELEASE_TO_REFRESH && mRefreshListener != null) {
                    changeState(STATE_PULL_REFRESHING);
                    mRefreshListener.onRefreshStarted();
                } else {
                    if (mState == STATE_PULL_TO_REFRESH) {
                        changeState(STATE_INIT);
                    }
                }
            }
            break;
        }
        return true;
    }

    private void doWithPullEvents() {
        // 计算滑动的距离
        int scrollValue =  Math.round(Math.min(mInitialMotionY - mLastMotionX, 0) / 2f);
        scrollHeader(scrollValue);

        if (!isRefreshing()) {
            if (mState != STATE_PULL_TO_REFRESH && Math.abs(scrollValue) < mPullRefreshLayout.getHeight()) {
                changeState(STATE_PULL_TO_REFRESH);
            } else if (mState == STATE_PULL_TO_REFRESH && Math.abs(scrollValue) > mPullRefreshLayout.getHeight()){
                changeState(STATE_RELEASE_TO_REFRESH);
            }
        }
    }

    private void changeState(int state) {
        mState = state;
        switch(mState) {
        case STATE_INIT:
            scrollHeader(0);
            break;
        case STATE_PULL_TO_REFRESH:
            mPullRefreshLayout.setVisibility(View.VISIBLE);
            break;
        case STATE_RELEASE_TO_REFRESH:
            break;
        case STATE_PULL_REFRESHING:
        case STATE_MANUAL_REFRESHING:
            if (mIsRefreshingLayoutInit) {
                mRefreshingLayout.setVisibility(View.VISIBLE);
                mPullRefreshLayout.setVisibility(View.GONE);
            }
            scrollHeader(-mPullRefreshLayout.getHeight());
            break;
        }
    }

    private void scrollHeader(int value) {
        scrollTo(0, value);
    }

    private boolean isRefreshing() {
        return mState == STATE_PULL_REFRESHING || mState == STATE_MANUAL_REFRESHING;
    }
}