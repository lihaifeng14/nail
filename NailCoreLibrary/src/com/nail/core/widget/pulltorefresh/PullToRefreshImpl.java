package com.nail.core.widget.pulltorefresh;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.nail.core.R;
import com.nail.core.widget.pulltorefresh.IPullToRefresh.RefreshListener;

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

    private Handler mHandler;
    private Interpolator mScrollInterpolator;
    private SmoothScrollRunnable mSmoothScrollRunnable;

    public PullToRefreshImpl(Context context, AttributeSet attrs, PullBehaviorListener listener) {
        mPullBehaviorListener = listener;
        mContext = context;

        mHandler = new Handler(mContext.getMainLooper());
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
        mHeaderHeight = mContext.getResources().getDimensionPixelSize(R.dimen.pulltorefresh_header_height);
        mPullRefreshLayout.switchState(STATE_INIT);

        mRefreshingLayout = (PullToRefreshHeader)inflater.inflate(R.layout.pulltorefresh_header, null);
        mRefreshingLayout.switchState(STATE_PULL_REFRESHING);

        mParentView.addView(mPullRefreshLayout, 0);
        mParentView.setPadding(0, -mHeaderHeight, 0, 0);
    }

    private void scrollHeader(int value) {
        mParentView.scrollTo(0, value);
    }

    private void smoothScrollHeader(int value) {
        smoothScrollTo(mParentView, value, 500);
    }

    private final void smoothScrollTo(View view, int newScrollValue, long duration) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        final int oldScrollValue = view.getScrollY();
        if (oldScrollValue != newScrollValue) {
            if (null == mScrollInterpolator) {
                mScrollInterpolator = new DecelerateInterpolator();
            }
            mSmoothScrollRunnable = new SmoothScrollRunnable(view, oldScrollValue, newScrollValue, duration);

            mHandler.post(mSmoothScrollRunnable);
        }
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
            if (mPullBehaviorListener.couldPullToRefresh() &&
                    (mRefreshListener != null && mRefreshListener.couldDoPullRefresh())) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;

        case MotionEvent.ACTION_MOVE:
            if (mPullBehaviorListener.couldPullToRefresh() &&
                    (mRefreshListener != null && mRefreshListener.couldDoPullRefresh())) {
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
            if (mPullBehaviorListener.couldPullToRefresh() &&
                    (mRefreshListener != null && mRefreshListener.couldDoPullRefresh())) {
                mInitialMotionY = mLastMotionY = ev.getY();
                mInitialMotionX = mLastMotionX = ev.getX();
                mIsBeingDragged = false;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (mPullBehaviorListener.couldPullToRefresh() &&
                    (mRefreshListener != null && mRefreshListener.couldDoPullRefresh())) {
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
                        smoothScrollHeader(0);
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
            smoothScrollHeader(0);
            break;
        case STATE_PULL_TO_REFRESH:
            break;
        case STATE_RELEASE_TO_REFRESH:
            break;
        case STATE_PULL_REFRESHING:
        case STATE_MANUAL_REFRESHING:
            mPullRefreshLayout.setVisibility(View.INVISIBLE);
            scrollHeader(mParentView.getScrollY()+mHeaderHeight);
            mPullBehaviorListener.addPullHeaderView(mRefreshingLayout);
            smoothScrollHeader(0);
            break;
        }
    }

    private class SmoothScrollRunnable implements Runnable {

        private View mView;
        private int mFromValue;
        private int mToValue;
        private int mCurValue;
        private long mDuration;
        private long mStartTime;
        private boolean mIsCancel;

        public SmoothScrollRunnable(View view, int oldValue, int newValue, long duration) {
            mView = view;
            mFromValue = oldValue;
            mToValue = newValue;
            mIsCancel = false;
            mStartTime = -1;
            mCurValue = -1;
            mDuration = duration;
        }

        public void stop() {
            mIsCancel = true;
        }

        @Override
        public void run() {
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {
                long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math.round((mFromValue - mToValue)
                        * mScrollInterpolator.getInterpolation(normalizedTime / 1000f));
                mCurValue = mFromValue - deltaY;
                mView.scrollTo(0, mCurValue);
            }

            if (!mIsCancel && mToValue != mCurValue) {
                mHandler.postDelayed(this, 16);
            }
        }
    }
}