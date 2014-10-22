package com.nail.news.manager;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.nail.core.http.AsyncHttpHandler;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.core.http.IHttpResult;
import com.nail.news.NailNewsApplication;
import com.nail.news.NailNewsRequest;

public abstract class BaseManager implements IHttpResult {

    protected AsyncHttpHandler mHttpHandler;
    protected Handler mHandler;
    protected static NailNewsRequest mUrlManager;

    protected CacheManager mCacheManager;

    protected Map<Integer, Object> mMapRequest;
    protected Map<Integer, Object> mMapRequestTag;

    protected void init(Context context) {

        mHttpHandler = NailNewsApplication.getInstance().getHttpHandler();
        mHandler = new Handler(context.getMainLooper());
        mMapRequest = new HashMap<Integer, Object>();
        mMapRequestTag = new HashMap<Integer, Object>();
        mCacheManager = CacheManager.getInstance();
        mUrlManager = NailNewsRequest.getInstance();

    }

    public void onMainRequestSuccess(int id, IBaseContent content) {
        mMapRequest.remove(id);
        mMapRequestTag.remove(id);
    }

    public void onMainRequestFailed(int id, HttpException e) {
        mMapRequest.remove(id);
        mMapRequestTag.remove(id);
    }

    @Override
    public void onRequestSuccess(final int id, final IBaseContent content) {
        Log.d("lihaifeng", "Request Success " + id);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onMainRequestSuccess(id, content);
            }
        });
    }

    @Override
    public void onRequestFailed(final int id, final HttpException e) {
        Log.d("lihaifeng", "Request Failed " + id);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onMainRequestFailed(id, e);
            }
        });
    }
}