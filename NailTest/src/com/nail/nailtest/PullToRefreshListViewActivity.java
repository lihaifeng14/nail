package com.nail.nailtest;

import com.nail.core.widget.pulltorefresh.IPullToLoad.LoadListener;
import com.nail.core.widget.pulltorefresh.IPullToRefresh.RefreshListener;
import com.nail.core.widget.pulltorefresh.PullToRefreshListView;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PullToRefreshListViewActivity extends Activity {

    PullToRefreshListView mListView;
    Handler mMainHandler;

    private int mCount = 20;
    private TestAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulltorefreshlistview);
        mListView = (PullToRefreshListView)findViewById(R.id.list_test);
        mListView.initPullToRefresh();

        mMainHandler = new Handler();

        mAdapter = new TestAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setRefreshListener(new RefreshListener() {
            @Override
            public void onRefreshStarted() {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 4000);
            }
            @Override
            public void couldDoPullRefresh() {
            }
        });
        mListView.setLoadListener(new LoadListener() {
            
            @Override
            public void onLoadStarted() {
                mMainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mCount += 5;
                        mAdapter.notifyDataSetChanged();
                        mListView.onLoadComplete();
                    }
                }, 2000);
            }
        });
    }

    public class TestAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(PullToRefreshListViewActivity.this).inflate(R.layout.list_item, null);
            }
            TextView text = (TextView)convertView.findViewById(R.id.text_info);
            text.setText("Line " + (position+1));
            return convertView;
        }
    }
}
