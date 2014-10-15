package com.nail.core.widget.pulltorefresh;

public interface IPullToLoad {

    public static interface LoadListener {
        public void onLoadStarted();
    }
    public void setLoadListener(LoadListener listener);

    public void onLoadComplete();
}