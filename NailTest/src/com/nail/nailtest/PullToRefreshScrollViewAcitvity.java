package com.nail.nailtest;

import com.nail.core.widget.pulltorefresh.IPullToRefresh.RefreshListener;
import com.nail.core.widget.pulltorefresh.PullToRefreshScrollView;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;

public class PullToRefreshScrollViewAcitvity extends Activity {

    PullToRefreshScrollView mScrollView;
    Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulltorefreshscrollview);
        mScrollView = (PullToRefreshScrollView)findViewById(R.id.scroll_test);
        mScrollView.initPullToRefresh();

        mMainHandler = new Handler();

        mScrollView.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefreshStarted() {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScrollView.onRefreshComplete();
                    }
                }, 2000);
            }
            @Override
            public void couldDoPullRefresh() {
            }
        });
    }
}
