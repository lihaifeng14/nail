package com.nail.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class PullToRefreshBaseView<T extends View> extends LinearLayout {

    public static final int STATE_INIT = 0;
    public static final int STATE_PULL_TO_REFRESH = 1;
    public static final int STATE_RELEASE_TO_REFRESH = 2;
    public static final int STATE_PULL_REFRESHING = 3;
    public static final int STATE_MANUAL_REFRESHING = 4;

    private int mTouchSlop;
    private float mLastMotionX, mLastMotionY;

    private boolean mIsBeingDragged = false;
    private T mInternalView;

    private int mState;

    protected abstract T createPullToRefreshView(Context context, AttributeSet attrs);

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
        addInternalView();
    }

    private void addInternalView() {
        setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams param = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mInternalView, param);
    }

    
}