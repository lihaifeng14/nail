package com.nail.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nail.core.R;
import com.nail.core.widget.IPullToRefresh.RefreshListener;

public class PullToRefreshImpl {

    public static final int STATE_INIT                  = 0;
    public static final int STATE_PULL_TO_REFRESH       = 1;
    public static final int STATE_RELEASE_TO_REFRESH    = 2;
    public static final int STATE_PULL_REFRESHING       = 3;
    public static final int STATE_MANUAL_REFRESHING     = 4;

    public interface PullBehaviorListener {
        public boolean couldPullToRefresh();
        public void addPullHeaderView(View header);
        public void removePullHeaderView(View header);
        public void showScrollBar(boolean show);
    }

    private Context mContext;
    private int mTouchSlop;
    private float mInitialMotionX, mInitialMotionY;
    private float mLastMotionX, mLastMotionY;

    private boolean mIsBeingDragged = false;
    private int mHeaderHeight;

    private PullToRefreshHeader mPullRefreshLayout;
    private PullToRefreshHeader mRefreshingLayout;
    private ViewGroup mParentView;

    private int mOldState;
    private int mState;
    private RefreshListener mRefreshListener;
    private PullBehaviorListener mPullBehaviorListener;

    public PullToRefreshImpl(Context context, AttributeSet attrs, PullBehaviorListener listener) {
        mPullBehaviorListener = listener;
        mContext = context;

        init(attrs);
    }

    private void init(AttributeSet attrs) {
        ViewConfiguration config = ViewConfiguration.get(mContext);
        mTouchSlop = config.getTouchSlop();
    }

    public void initPullToRefresh(ViewGroup parent) {
        mParentView = parent;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mPullRefreshLayout = (PullToRefreshHeader)inflater.inflate(R.layout.pulltorefresh_header, null);
        measureView(mPullRefreshLayout);
        mHeaderHeight = mPullRefreshLayout.getMeasuredHeight();
        mPullRefreshLayout.switchState(STATE_INIT);

        mRefreshingLayout = (PullToRefreshHeader)inflater.inflate(R.layout.pulltorefresh_header, null);
        mRefreshingLayout.switchState(STATE_PULL_REFRESHING);

        mParentView.addView(mPullRefreshLayout, 0);
        mParentView.setPadding(0, -mHeaderHeight, 0, 0);
    }

    private void scrollHeader(int value) {
        mParentView.scrollTo(0, value);
    }

    private boolean isRefreshing() {
        return mState == STATE_PULL_REFRESHING || mState == STATE_MANUAL_REFRESHING;
    }

    public void onRefreshComplete() {
        if (isRefreshing()) {
            changeState(STATE_INIT);
        }
    }

    public void setRefreshListener(RefreshListener listener) {
        mRefreshListener = listener;
    }

    public void setRefreshing(boolean show) {
        if (show) {
            if (isRefreshing()) {
                return;
            }
            changeState(STATE_MANUAL_REFRESHING);
        } else {
            if (isRefreshing()) {
                changeState(STATE_INIT);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        Log.d("lihaifeng", "Intercept Action " + action);

        if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)) {
            mIsBeingDragged = false;
            return false;
        }
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true;
        }

        switch(action) {
        case MotionEvent.ACTION_DOWN:
            if (mPullBehaviorListener.couldPullToRefresh()) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (mPullBehaviorListener.couldPullToRefresh()) {
                float deltaY = ev.getY()-mLastMotionY;
                float deltaX = ev.getX()-mLastMotionX;
                if (!mIsBeingDragged && deltaY > mTouchSlop && Math.abs(deltaY) > Math.abs(deltaX)) {
                    mIsBeingDragged = true;
                }
            }
            break;
        }
        Log.d("lihaifeng", "Intercept return " + mIsBeingDragged);
        return mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        Log.d("lihaifeng", "Touch Action " + action);

        switch(action) {
        case MotionEvent.ACTION_DOWN:
            if (mPullBehaviorListener.couldPullToRefresh()) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (mPullBehaviorListener.couldPullToRefresh()) {
                float deltaY = ev.getY()-mLastMotionY;
                float deltaX = ev.getX()-mLastMotionX;
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
                    if (isRefreshing()) {
                        scrollHeader(0);
                    } else {
                        changeState(STATE_INIT);
                    }
                }
            }
            break;
        }
        return mIsBeingDragged;
    }

    private void doWithPullEvents() {
        // 计算滑动的距离
        int scrollValue =  Math.round(Math.min(mInitialMotionY - mLastMotionY, 0) / 2f);

        if (!isRefreshing()) {
            if (mState != STATE_PULL_TO_REFRESH && Math.abs(scrollValue) < mHeaderHeight) {
                changeState(STATE_PULL_TO_REFRESH);
            } else if (mState == STATE_PULL_TO_REFRESH && Math.abs(scrollValue) > mHeaderHeight){
                changeState(STATE_RELEASE_TO_REFRESH);
            }
        }

        scrollHeader(scrollValue);
    }

    private void changeState(int state) {
        mOldState = mState;
        mState = state;

        mPullRefreshLayout.switchState(state);

        switch(mState) {
        case STATE_INIT:
            mPullRefreshLayout.setVisibility(View.VISIBLE);
            mPullBehaviorListener.removePullHeaderView(mRefreshingLayout);
            scrollHeader(0);
            break;
        case STATE_PULL_TO_REFRESH:
            break;
        case STATE_RELEASE_TO_REFRESH:
            break;
        case STATE_PULL_REFRESHING:
        case STATE_MANUAL_REFRESHING:
            mPullRefreshLayout.setVisibility(View.INVISIBLE);
            mPullBehaviorListener.addPullHeaderView(mRefreshingLayout);
            scrollHeader(0);
            break;
        }
    }

    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int childHeightSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.height);

        try {
            child.measure(childWidthSpec, childHeightSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}