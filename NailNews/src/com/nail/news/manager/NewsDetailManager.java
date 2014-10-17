package com.nail.news.manager;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;

import com.nail.core.http.AsyncHttpRequest;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.news.data.NewsDetailData;
import com.nail.news.manager.CacheManager.CacheListener;

public class NewsDetailManager extends BaseManager implements CacheListener {

    protected static NewsDetailManager mInstance;

    public interface NewDetailCallback {
        public void onDetailCallback(NewsDetailData data);
        public void onDetailFailed();
    }

    public static NewsDetailManager getInstance() {
        if (mInstance == null) {
            mInstance = new NewsDetailManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        super.init(context);
    }

    public void getDetailData(String documentId, NewDetailCallback listener) {
        String url = mUrlManager.getDetailUrl(documentId);
        AsyncHttpRequest request;
        try {
            request = mHttpHandler.creatGetRequest(new URI(url),
                    NewsDetailData.class, this);
            mMapRequest.put(request.getRequestId(), listener);
            request.setAdjustContent();
            mHttpHandler.sendRequest(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCacheFailed(String tag) {
    }

    @Override
    public void onCacheSuccess(String tag, IBaseContent content) {
    }

    @Override
    public void onMainRequestFailed(int id, HttpException e) {
        NewDetailCallback listener = (NewDetailCallback)mMapRequest.get(id);
        super.onMainRequestFailed(id, e);
        if (listener == null) {
            return;
        }

        listener.onDetailFailed();
    }

    @Override
    public void onMainRequestSuccess(int id, IBaseContent content) {
        NewDetailCallback listener = (NewDetailCallback)mMapRequest.get(id);
        super.onMainRequestSuccess(id, content);

        if (listener == null) {
            return;
        }
        if (content == null || !(content instanceof NewsDetailData)) {
            return;
        }

        NewsDetailData data = (NewsDetailData)content;
        listener.onDetailCallback(data);
    }
}