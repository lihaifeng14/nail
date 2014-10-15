package com.nail.core.widget.pulltorefresh;

import com.nail.core.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PullToRefreshListView extends ListView implements IPullToRefresh, IPullToLoad,
    PullToRefreshImpl.PullBehaviorListener, OnScrollListener {

    private PullToRefreshImpl mPullToRefreshImpl;
    private ListAdapter mAdapter;
    private View mPullToLoadFooter;

    private OnScrollListener mScrollListener;
    private LoadListener mLoadListener;
    private int mLastVisibleItem = 0;

    public PullToRefreshListView(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPullToRefreshImpl = new PullToRefreshImpl(context, attrs, this);

        mPullToLoadFooter = LayoutInflater.from(context).inflate(R.layout.pulltoload_footer, null);
        addFooterView(mPullToLoadFooter);
        super.setOnScrollListener(this);
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
        ViewGroup parent = (ViewGroup)getParent();
        mPullToRefreshImpl.initPullToRefresh(parent);
    }

    // 实现IPullToLoad的接口
    @Override
    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    @Override
    public void onLoadComplete() {
        removeFooterView(mPullToLoadFooter);
    }

    // 实现PullBehaviorListener的接口
    @Override
    public boolean couldPullToRefresh() {
        if (null == mAdapter || mAdapter.isEmpty()) {
            return true;
        } else {
            if (getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= getTop();
                }
            }
        }
        return false;
    }

    @Override
    public void showScrollBar(boolean show) {
        super.setVerticalScrollBarEnabled(show);
    }

    @Override
    public void addPullHeaderView(View header) {
        addHeaderView(header);
    }

    @Override
    public void removePullHeaderView(View header) {
        boolean ret = removeHeaderView(header);
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

    @Override
    public void setAdapter(ListAdapter adapter) {
        mAdapter = adapter;
        super.setAdapter(adapter);
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            if (mAdapter != null && mLastVisibleItem >= mAdapter.getCount()) {
                if (mLoadListener != null) {
                    mLoadListener.onLoadStarted();
                }
            }
        }
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        mLastVisibleItem = firstVisibleItem + visibleItemCount - 1;
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}