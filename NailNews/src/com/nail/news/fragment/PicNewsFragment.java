package com.nail.news.fragment;

import com.nail.core.widget.pulltorefresh.IPullToLoad;
import com.nail.core.widget.pulltorefresh.IPullToRefresh;
import com.nail.core.widget.pulltorefresh.PullToRefreshListView;
import com.nail.news.R;
import com.nail.news.adapter.NewsFragmentAdapter;
import com.nail.news.manager.NewsFragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nail.news.data.PageContent;

public class PicNewsFragment extends BaseFragment implements PageContent.NewsDataListener,
        IPullToRefresh.RefreshListener, IPullToLoad.LoadListener{

    private PullToRefreshListView mListView;
    protected int mType;
    private NewsFragmentAdapter mAdapter;
    private NewsFragmentManager mManager;
    private boolean mIsPulltoRefresh;
    private boolean mIsAutoRefresh;
    private boolean mIsLoadingMore;

    public void setType(int type) {
        mType = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = NewsFragmentManager.getInstance();
        mManager.addListener(mType, this);
        mIsPulltoRefresh = false;
        mIsAutoRefresh = false;
    }

    @Override
    public void onDestroy() {
        mManager.removeListener(mType, this);
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    // 显示时加载最新数据
    public void onFragmentShow() {
        if (mManager.needRefreshData(mType)) {
            Log.d("lihaifeng", "Need to refresh");
            mManager.loadFirstData(mType);
            if (mListView != null) {
                mIsAutoRefresh = true;
                mListView.setRefreshing(true);
            }
        } else {
            Log.d("lihaifeng", "No need to refresh");
        }
    }

    // 创建时只加载缓存数据
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newspic, null);
        mListView = (PullToRefreshListView)view.findViewById(R.id.fragment_list);
        mListView.initPullToRefresh();
        mListView.setRefreshListener(this);
        mListView.setLoadListener(this);

        mAdapter = new NewsFragmentAdapter(mActivity);
        mListView.setAdapter(mAdapter);
        mAdapter.setParentView((ViewGroup)view);

        mManager.loadDefaultData(mType);
 
        super.onCreateView();
        return view;
    }

    @Override
    public void onNewsData(int type, PageContent content) {
        if (mType != type) {
            return;
        }

        if (mIsPulltoRefresh && mListView != null) {
            mListView.onRefreshComplete();
        }
        if (mIsAutoRefresh && mListView != null) {
            mListView.setRefreshing(false);
        }

        if (mAdapter != null) {
            mAdapter.setContent(content);
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoadStarted() {
        mIsLoadingMore = true;
        mManager.loadMoreData(mType);
    }

    @Override
    public void onRefreshStarted() {
        Log.d("lihaifeng", "start pull to fresh");
        mManager.loadFirstData(mType);
        mIsPulltoRefresh = true;
    }

    @Override
    public boolean couldDoPullRefresh() {
        return true;
    }

    @Override
    public void onNewsFailed(int type) {
        if (mType != type) {
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
    public void onNoMoreData(int type) {
        if (mType != type) {
            return;
        }
        if (mIsLoadingMore && mListView != null) {
            mListView.onLoadComplete();
        }
    }

	@Override
	public void onNoNewData(int type) {
        if (mType != type) {
            return;
        }

        if (mIsPulltoRefresh && mListView != null) {
            mListView.onRefreshComplete();
        }
        if (mIsAutoRefresh && mListView != null) {
            mListView.setRefreshing(false);
        }
	}
}