package com.nail.news.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.nail.core.widget.pulltorefresh.IPullToLoad;
import com.nail.core.widget.pulltorefresh.IPullToRefresh;
import com.nail.core.widget.pulltorefresh.PullToRefreshListView;
import com.nail.news.R;
import com.nail.news.adapter.CommentsAdapter;
import com.nail.news.data.CommentsContent;
import com.nail.news.data.CommentsContent.CommentDataListener;
import com.nail.news.manager.CommentsManager;

public class CommentsActivity extends Activity implements CommentDataListener,
        IPullToRefresh.RefreshListener, IPullToLoad.LoadListener, View.OnClickListener {

    public static final String EXTRA_COMMENTS_URL = "extra_comments_url";
    private String mCommentsUrl;
    private PullToRefreshListView mListView;
    private CommentsAdapter mAdapter;
    private CommentsManager mManager;
    private boolean mIsPulltoRefresh;
    private boolean mIsAutoRefresh;
    private boolean mIsLoadingMore;

    private View mBackView;
    private View mCommentsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comments);
        mBackView = findViewById(R.id.button_back);
        mBackView.setOnClickListener(this);
        mCommentsView = findViewById(R.id.button_comments);
        mCommentsView.setVisibility(View.GONE);

        mCommentsUrl = getIntent().getStringExtra(EXTRA_COMMENTS_URL);
        mManager = CommentsManager.getInstance();
        mManager.addListener(mCommentsUrl, this);
        mIsPulltoRefresh = false;
        mIsAutoRefresh = false;
        mIsLoadingMore = false;

        mListView = (PullToRefreshListView) findViewById(R.id.comments_list);
        mListView.initPullToRefresh();
        mListView.setRefreshListener(this);
        mListView.setLoadListener(this);

        mAdapter = new CommentsAdapter(this);
        mListView.setAdapter(mAdapter);

        mManager.loadFirstData(mCommentsUrl);
        if (mListView != null) {
            mIsAutoRefresh = true;
            mListView.setRefreshing(true);
        }
    }

    @Override
    protected void onDestroy() {
        mManager.removeListener(mCommentsUrl, this);
        super.onDestroy();
    }

    @Override
    public void onNewsData(String url, CommentsContent content) {
        if (mCommentsUrl.compareTo(url) != 0) {
            return;
        }

        if (mIsPulltoRefresh && mListView != null) {
            mListView.onRefreshComplete();
        }
        if (mIsAutoRefresh && mListView != null) {
            mListView.setRefreshing(false);
        }

        mAdapter.setContent(content);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewsFailed(String url) {
        if (mCommentsUrl.compareTo(url) != 0) {
            return;
        }

        if (mIsPulltoRefresh && mListView != null) {
            mListView.onRefreshComplete();
        }
        if (mIsAutoRefresh && mListView != null) {
            mListView.setRefreshing(false);
        }
        if (mIsLoadingMore && mListView != null) {
            mListView.onLoadComplete();
        }
    }

    @Override
    public void onNoMoreData(String url) {
        if (mCommentsUrl.compareTo(url) != 0) {
            return;
        }
        if (mIsLoadingMore && mListView != null) {
            mListView.onLoadComplete();
        }
    }

    @Override
    public void onLoadStarted() {
        mIsLoadingMore = true;
        mManager.loadMoreData(mCommentsUrl);
    }

    @Override
    public void onRefreshStarted() {
        mIsPulltoRefresh = true;
        mManager.loadFirstData(mCommentsUrl);
    }

    @Override
    public boolean couldDoPullRefresh() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.button_back:
            finish();
            break;
        }
    }
}