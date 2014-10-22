package com.nail.news.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nail.core.http.IBaseContent;
import com.nail.news.NailNewsRequest;
import com.nail.news.data.CommentsData.CommentsAllData;

public class CommentsContent {
    private String mCommentUrl;

    public interface CommentDataListener {
        public void onNewsData(String url, CommentsContent content);
        public void onNewsFailed(String url);
        public void onNoMoreData(String url);
    }

    public Set<CommentDataListener> mListeners;

    public int mPageIndex; // 0表示没有任何内容

    public List<CommentsDetailData> mNewestData;
    public List<CommentsDetailData> mHottestData;
    public int mCount;
    public int mJoinCount;

    public CommentsContent(String url) {
        mCommentUrl = url;
        mPageIndex = 0;
        mListeners = new HashSet<CommentDataListener>();
    }

    public String getFirstRequestUrl() {
        mPageIndex = 0;
        return getRequestUrl();
    }

    public String getRequestUrl() {
        int page = mPageIndex+1;
        return NailNewsRequest.getInstance().getCommentsUrl(mCommentUrl, page);
    }

    public void setFirstData(IBaseContent content) {
        if (content == null || !(content instanceof CommentsData)) {
            return;
        }

        CommentsData data = (CommentsData)content;
        if (data.getData() == null) {
            return;
        }

        CommentsAllData allData = (CommentsAllData)data.getData();
        mCount = allData.getCount();
        mJoinCount = allData.getJoin_count();
        ArrayList<CommentsDetailData> newest = allData.getComments().getNewest();
        ArrayList<CommentsDetailData> hotest = allData.getComments().getHottest();

        mNewestData = new ArrayList<CommentsDetailData>();
        if (newest != null && newest.size() > 0) {
            mNewestData.addAll(newest);
        }

        mHottestData = new ArrayList<CommentsDetailData>();
        if (hotest != null && hotest.size() > 0) {
            mHottestData.addAll(hotest);
        }
        mPageIndex = 1;
    }

    public boolean addData(IBaseContent content) {
        if (content == null || !(content instanceof CommentsData)) {
            return false;
        }

        CommentsData data = (CommentsData)content;
        if (data.getData() == null) {
            return false;
        }

        CommentsAllData allData = (CommentsAllData)data.getData();
        mCount = allData.getCount();
        mJoinCount = allData.getJoin_count();
        ArrayList<CommentsDetailData> newest = allData.getComments().getNewest();
        ArrayList<CommentsDetailData> hotest = allData.getComments().getHottest();

        if ((newest == null || newest.size() == 0) && (hotest == null || hotest.size() == 0)) {
            return false;
        }

        if (hotest != null && hotest.size() > 0) {
            mHottestData.addAll(hotest);
        }

        if (newest != null && newest.size() > 0) {
            mNewestData.addAll(newest);
        }

        mPageIndex += 1;
        return true;
    }

    public boolean isHeader(int position) {
        if (position == 0) {
            return true;
        }
        if (mHottestData != null && mHottestData.size() > 0) {
            if (position == mHottestData.size()+1) {
                return true;
            }
        }
        return false;
    }

    public void setDataFailed() {
    }

    public int getItemCount() {
        int itemCount = 0;
        if (mNewestData != null && mNewestData.size() > 0) {
            itemCount += 1;
            itemCount += mNewestData.size();
        }
        if (mHottestData != null && mHottestData.size() > 0) {
            itemCount += 1;
            itemCount += mHottestData.size();
        }
        return itemCount;
    }

    public void addListener(CommentDataListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(CommentDataListener listener) {
        mListeners.remove(listener);
    }

    public void notifyNewData() {
        for (CommentDataListener listener : mListeners) {
            listener.onNewsData(mCommentUrl, this);
        }
    }

    public void notifyNoMoreData() {
        for (CommentDataListener listener : mListeners) {
            listener.onNoMoreData(mCommentUrl);
        }
    }

    public void notifyFailed() {
        for (CommentDataListener listener : mListeners) {
            listener.onNewsFailed(mCommentUrl);
        }
    }
}