package com.nail.core.widget.pulltorefresh;

import com.nail.core.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PullToRefreshScrollView extends ScrollView implements IPullToRefresh,
    PullToRefreshImpl.PullBehaviorListener {

    private PullToRefreshImpl mPullToRefreshImpl;
    private LinearLayout mScrollLinearLayout;

    public PullToRefreshScrollView(Context context) {
        super(context);
        init(context, null);
    }
    
    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPullToRefreshImpl = new PullToRefreshImpl(context, attrs, this);
    }

    // 实现PullBehaviorListener的接口
    @SuppressLint("NewApi")
    @Override
    public boolean couldPullToRefresh() {
        int ret = getScrollY();
        return ret == 0;
    }

    @Override
    public void addPullHeaderView(View header) {
        mScrollLinearLayout.addView(header, 0);
    }

    @Override
    public void removePullHeaderView(View header) {
        mScrollLinearLayout.removeView(header);
    }

    @Override
    public void showScrollBar(boolean show) {
        setVerticalScrollBarEnabled(show);
    }

    // 实现IPullToRefresh接口
    @Override
    public void setRefreshListener(RefreshListener listener) {
        mPullToRefreshImpl.setRefreshListener(listener);
    }

    @Override
    public void setRefreshing(boolean show) {
        mPullToRefreshImpl.setRefreshing(show);
    }

    @Override
    public void onRefreshComplete() {
        mPullToRefreshImpl.onRefreshComplete();
    }

    @Override
    public void initPullToRefresh() {
        mScrollLinearLayout = (LinearLayout)findViewById(R.id.pulltorefresh_scroll_linearlayout);
        ViewGroup parent = (ViewGroup)getParent();
        mPullToRefreshImpl.initPullToRefresh(parent);
    }

    // 重新实现基类的方法
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = mPullToRefreshImpl.onInterceptTouchEvent(ev);
        return ret || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = mPullToRefreshImpl.onTouchEvent(ev);
        return ret || super.onTouchEvent(ev);
    }
}