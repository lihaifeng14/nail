package com.nail.core.widget.pulltorefresh;

import com.nail.core.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
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
        mImage.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        switch(state) {
        case PullToRefreshImpl.STATE_INIT:
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageResource(R.drawable.pull_to_refresh);
            mTextInfo.setText(R.string.refreshing);
            break;
        case PullToRefreshImpl.STATE_PULL_TO_REFRESH:
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageResource(R.drawable.pull_to_refresh);
            mTextInfo.setText(R.string.pull_to_refresh);
            break;
        case PullToRefreshImpl.STATE_RELEASE_TO_REFRESH:
            mImage.setVisibility(View.VISIBLE);
            mImage.setImageResource(R.drawable.release_to_fresh);
            mTextInfo.setText(R.string.release_to_refresh);
            break;
        case PullToRefreshImpl.STATE_PULL_REFRESHING:
        case PullToRefreshImpl.STATE_MANUAL_REFRESHING:
            mProgressBar.setVisibility(View.VISIBLE);
            mTextInfo.setText(R.string.refreshing);
            break;
        }
    }

    @Override
    public void onFinishInflate() {
        init();
    }
}