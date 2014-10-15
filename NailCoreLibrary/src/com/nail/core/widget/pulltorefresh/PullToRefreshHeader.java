package com.nail.core.widget.pulltorefresh;

import com.nail.core.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PullToRefreshHeader extends RelativeLayout{

    private ImageView mImage;
    private ProgressBar mProgressBar;
    private TextView mTextInfo;

    public PullToRefreshHeader(Context context) {
        super(context);
    }

    public PullToRefreshHeader(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public PullToRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void init() {
        mProgressBar = (ProgressBar)findViewById(R.id.pulltofresh_header_progess);
        mImage = (ImageView)findViewById(R.id.pulltofresh_header_image);
        mTextInfo = (TextView)findViewById(R.id.pulltofresh_header_text);
    }

    public void switchState(int state) {
        switch(state) {
        case PullToRefreshImpl.STATE_INIT:
            mTextInfo.setText("Pull to refresh");
            break;
        case PullToRefreshImpl.STATE_PULL_TO_REFRESH:
            mTextInfo.setText("Pull to refresh");
            break;
        case PullToRefreshImpl.STATE_RELEASE_TO_REFRESH:
            mTextInfo.setText("Release to refresh");
            break;
        case PullToRefreshImpl.STATE_PULL_REFRESHING:
        case PullToRefreshImpl.STATE_MANUAL_REFRESHING:
            mTextInfo.setText("Refreshing");
            break;
        }
    }

    @Override
    public void onFinishInflate() {
        init();
    }
}