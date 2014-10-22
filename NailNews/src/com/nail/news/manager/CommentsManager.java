package com.nail.news.manager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.nail.core.http.AsyncHttpRequest;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.news.data.CommentsContent;
import com.nail.news.data.CommentsContent.CommentDataListener;
import com.nail.news.data.CommentsData;
import com.nail.news.manager.CacheManager.CacheListener;

public class CommentsManager extends BaseManager implements CacheListener {

    protected static CommentsManager mInstance;
    private Map<String, CommentsContent> mMapComments;

    public static CommentsManager getInstance() {
        if (mInstance == null) {
            mInstance = new CommentsManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        super.init(context);
        mMapComments = new HashMap<String, CommentsContent>();
    }

    public void loadFirstData(String commentUrl) {
        CommentsContent content = mMapComments.get(commentUrl);
        if (content != null) {
            String url = content.getFirstRequestUrl();
            try {
                AsyncHttpRequest request = mHttpHandler.creatGetRequest(
                      new URI(url), CommentsData.class, this);
                mMapRequest.put(request.getRequestId(), commentUrl);
                mMapRequestTag.put(request.getRequestId(), true);
                request.setAdjustContent();
                mHttpHandler.sendRequest(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadMoreData(String commentUrl) {
        CommentsContent content = mMapComments.get(commentUrl);
        if (content != null) {
            String url = content.getRequestUrl();
            try {
                AsyncHttpRequest request = mHttpHandler.creatGetRequest(
                      new URI(url), CommentsData.class, this);
                mMapRequest.put(request.getRequestId(), commentUrl);
                mMapRequestTag.put(request.getRequestId(), false);
                request.setAdjustContent();
                mHttpHandler.sendRequest(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMainRequestSuccess(int id, IBaseContent content) {
        String url = (String)mMapRequest.get(id);
        Boolean isFirst = (Boolean)mMapRequestTag.get(id);
        super.onMainRequestSuccess(id, content);
        if (url == null || isFirst == null) {
            return;
        }

        CommentsContent data = mMapComments.get(url);
        if (isFirst) {
            data.setFirstData(content);
            data.notifyNewData();
        } else {
            boolean ret = data.addData(content);
            if (ret) {
                data.notifyNewData();
            } else {
                data.notifyNoMoreData();
            }
        }
    }

    @Override
    public void onMainRequestFailed(int id, HttpException e) {
        String url = (String)mMapRequest.get(id);
        Boolean isFirst = (Boolean)mMapRequestTag.get(id);
        super.onMainRequestFailed(id, e);
        if (url == null || isFirst == null) {
            return;
        }

        CommentsContent data = mMapComments.get(url);
        data.notifyFailed();
    }

    public void addListener(String commentUrl, CommentDataListener listener) {
        CommentsContent content = mMapComments.get(commentUrl);
        if (content == null) {
            content = new CommentsContent(commentUrl);
            mMapComments.put(commentUrl, content);
        }
        content.addListener(listener);
    }

    public void removeListener(String commentUrl, CommentDataListener listener) {
        CommentsContent content = mMapComments.get(commentUrl);
        mMapComments.remove(commentUrl);
        if (content != null) {
            content.removeListener(listener);
        }
    }

    @Override
    public void onCacheFailed(String tag) {
    }

    @Override
    public void onCacheSuccess(String tag, IBaseContent content) {
    }
}