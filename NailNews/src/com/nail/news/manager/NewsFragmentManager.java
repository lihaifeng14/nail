package com.nail.news.manager;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.nail.core.http.AsyncHttpRequest;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.news.data.PageContent;
import com.nail.news.data.PageContent.NewsDataListener;
import com.nail.news.data.PicNewsData;
import com.nail.news.manager.CacheManager.CacheListener;

public class NewsFragmentManager extends BaseManager implements CacheListener {

    protected static NewsFragmentManager mInstance;
    private PageManager mPageManager;

    public static NewsFragmentManager getInstance() {
        if (mInstance == null) {
            mInstance = new NewsFragmentManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        super.init(context);

        mPageManager = PageManager.getInstance();
    }

    public boolean needRefreshData(int type) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            return content.needRefreshData();
        }
        return false;
    }

    public void loadDefaultData(int type) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            String url = content.getFirstRequestUrl();
            mCacheManager.getCache(url, String.valueOf(content.mType), PicNewsData.class, this);
        }
    }

    public void loadFirstData(int type) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            String url = content.getFirstRequestUrl();
            try {
                AsyncHttpRequest request = mHttpHandler.creatGetRequest(
                      new URI(url), PicNewsData.class, this);
                mMapRequest.put(request.getRequestId(), type);
                mMapRequestTag.put(request.getRequestId(), true);
                request.setSaveJsonFile(mCacheManager.getCaheFile(url));
                request.setAdjustContent();
                mHttpHandler.sendRequest(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadMoreData(int type) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            String url = content.getRequestUrl();
            try {
                AsyncHttpRequest request = mHttpHandler.creatGetRequest(
                      new URI(url), PicNewsData.class, this);
                mMapRequest.put(request.getRequestId(), type);
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
        Integer type = (Integer)mMapRequest.get(id);
        Boolean isFirst = (Boolean)mMapRequestTag.get(id);
        super.onMainRequestSuccess(id, content);
        if (type == null || isFirst == null) {
            return;
        }

        PageContent page = mPageManager.getPageByType(type);
        if (isFirst) {
            page.setFirstData(content, true);
            page.notifyNewData();
        } else {
            boolean ret = page.addData(content);
            if (ret) {
                page.notifyNewData();
            } else {
                page.notifyNoMoreData();
            }
        }
    }

    @Override
    public void onMainRequestFailed(int id, HttpException e) {
        Integer type = (Integer)mMapRequest.get(id);
        Boolean isFirst = (Boolean)mMapRequestTag.get(id);
        super.onMainRequestFailed(id, e);
        if (type == null || isFirst == null) {
            return;
        }

        PageContent page = mPageManager.getPageByType(type);
        page.notifyFailed();
    }

    @Override
    public void onCacheFailed(String tag) {
        Log.d("lihaifeng", "Cache Load Failed " + tag);
    }

    @Override
    public void onCacheSuccess(String tag, IBaseContent content) {
        Log.d("lihaifeng", "Cache Load Success " + tag);
        int type = Integer.valueOf(tag);
        PageContent page = mPageManager.getPageByType(type);
        page.setFirstData(content, false);
        page.notifyNewData();
    }

    public void addListener(int type, NewsDataListener listener) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            content.addListener(listener);
        }
    }

    public void removeListener(int type, NewsDataListener listener) {
        PageContent content = mPageManager.getPageByIndex(type);
        if (content != null) {
            content.removeListener(listener);
        }
    }
}