package com.nail.news.manager;

import java.io.File;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nail.core.http.IBaseContent;
import com.nail.core.utils.CommonUtils;
import com.nail.core.utils.MD5Utils;
import com.nail.core.utils.StorageUtils;

public class CacheManager {

    public static final String CACHE_FILE_DIR = "json";

    private File mCacheDirecotry;

    public interface CacheListener {
        public void onCacheFailed(String tag);
        public void onCacheSuccess(String tag, IBaseContent content);
    }

    protected static CacheManager mInstance;
    public static CacheManager getInstance() {
        if (mInstance == null) {
            mInstance = new CacheManager();
        }
        return mInstance;
    }

    public void init(Context context) {
        mCacheDirecotry = StorageUtils.getCacheDirectory(context, true, CACHE_FILE_DIR);
    }

    public File getCaheFile(String url) {
        String key = MD5Utils.getMD5(url);
        return new File(mCacheDirecotry, key);
    }

    public void getCache(String url, String tag, Class<? extends IBaseContent> cls, CacheListener listener) {
        File file = getCaheFile(url);
        String content = CommonUtils.getStringFromFile(file);
        if (content == null) {
            listener.onCacheFailed(tag);
        }

        IBaseContent ret = null;
        JSONObject json = JSON.parseObject(content);
        if (json != null) {
            ret = JSON.toJavaObject(json, cls);
        }
        listener.onCacheSuccess(tag, ret);
    }

}