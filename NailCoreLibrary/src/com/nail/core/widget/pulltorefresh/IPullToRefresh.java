package com.nail.core.widget.pulltorefresh;

public interface IPullToRefresh {

    public static interface RefreshListener {
        public void onRefreshStarted();
        public boolean couldDoPullRefresh();
    }

    public void setRefreshListener(RefreshListener listener);

    public void setRefreshing(boolean show);

    public void onRefreshComplete();

    public void initPullToRefresh();
}