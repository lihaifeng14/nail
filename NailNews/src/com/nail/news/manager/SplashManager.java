package com.nail.news.manager;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;

import com.nail.core.http.AsyncHttpRequest;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.news.data.SplashData;
import com.nail.news.data.SplashData.SplashInfo;

public class SplashManager extends BaseManager {

    protected static SplashManager mInstance;

    public interface SplashCallback {
        public void onSplashCallback(SplashInfo info);
        public void onSplashFailed();
    }

    public static SplashManager getInstance() {
        if (mInstance == null) {
            mInstance = new SplashManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        super.init(context);
    }

    public void getSplashInfo(SplashCallback listener) {
        String url = mUrlManager.getSplashUrl();
        AsyncHttpRequest request;
        try {
            request = mHttpHandler.creatGetRequest(new URI(url),
                    SplashData.class, this);
            mMapRequest.put(request.getRequestId(), listener);
            mHttpHandler.sendRequest(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMainRequestFailed(int id, HttpException e) {
        SplashCallback listener = (SplashCallback) mMapRequest.get(id);
        super.onMainRequestFailed(id, e);
        if (listener == null) {
            return;
        }

        listener.onSplashFailed();
    }

    @Override
    public void onMainRequestSuccess(int id, IBaseContent content) {
        SplashCallback listener = (SplashCallback) mMapRequest.get(id);
        super.onMainRequestSuccess(id, content);

        if (listener == null) {
            return;
        }
        if (content == null || !(content instanceof SplashData)) {
            return;
        }

        SplashData data = (SplashData)content;
        listener.onSplashCallback(data.getCover());
    }
}