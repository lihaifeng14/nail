package com.nail.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullToRefreshListView extends PullToRefreshBaseView<ListView> {

    public PullToRefreshListView(Context context) {
        super(context);
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public PullToRefreshListView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected ListView createPullToRefreshView(Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    protected boolean couldPullToRefresh() {
        return false;
    }
}