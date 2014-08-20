package com.nail.core.imageloader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.nail.core.utils.MD5Utils;
import com.nail.core.utils.StorageUtils;

import android.content.Context;
import android.util.Log;

public class DiskLruCache {

    private static final String DISK_CACHE_DIRECTORY = "thumbnails";
    private static final int INITIAL_CAPACITY = 64;

    private Context mContext;
    private File mDirectory;
    private long mTotalSize;
    private long mMaxCacheSize = 1024;
    private Map<String, CacheInfo> mLruEntries;

    public static DiskLruCache openCache(Context context, long maxCacheSize) {
        File directory = StorageUtils.getCacheDirectory(context, true, DISK_CACHE_DIRECTORY);
        Log.d("lihaifeng", "Disk cache " + directory.getAbsolutePath());
        return new DiskLruCache(context, directory, maxCacheSize);
    }

    private DiskLruCache(Context context, File directory, long maxCacheSize) {
        mContext = context;
        mDirectory = directory;
        if (maxCacheSize != -1) {
            mMaxCacheSize = maxCacheSize;
        }

        mLruEntries = Collections.synchronizedMap(new LinkedHashMap<String, CacheInfo>(INITIAL_CAPACITY, .75f, true));
        init();
    }

    private void init() {
        if (!mDirectory.exists()) {
            mDirectory.mkdirs();
            return;
        }

        mTotalSize = 0;

        File[] files = mDirectory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            CacheInfo info = new CacheInfo(file.getName(), file.length());
            putEntry(file.getName(), info);
        }
    }

    public synchronized File get(String url) {
        String key = MD5Utils.getMD5(url);
        CacheInfo info = mLruEntries.get(key);
        if (info == null) {
            return null;
        }
 
        File file = getCacheFile(key);
        if (!file.exists()) {
            return null;
        }

        return file;
    }

    public synchronized void put(String url, byte[] data) {
        String key = MD5Utils.getMD5(url);
        File file = getCacheFile(key);
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(file));
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        putEntry(key, new CacheInfo(key, file.length()));
        pruneInCaches();
    }

    private void pruneInCaches() {
        if (mTotalSize < mMaxCacheSize) {
            return;
        }

        Iterator<Map.Entry<String, CacheInfo>> iterator = mLruEntries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheInfo> entry = iterator.next();
            CacheInfo info = entry.getValue();
            boolean deleted = getCacheFile(info.mKey).delete();
            if (deleted) {
                mTotalSize -= info.mSize;
            }
            iterator.remove();

            if (mTotalSize < mMaxCacheSize) {
                break;
            }
        }
    }

    private File getCacheFile(String key) {
        return new File(mDirectory, key);
    }

    private void putEntry(String key, CacheInfo entry) {
        if (!mLruEntries.containsKey(key)) {
            mTotalSize += entry.mSize;
        } else {
            CacheInfo oldEntry = mLruEntries.get(key);
            mTotalSize += (entry.mSize - oldEntry.mSize);
        }
        mLruEntries.put(key, entry);
    }

    public static class CacheInfo {
        public String mKey;
        public long mSize;
        public CacheInfo(String key, long size) {
            mKey = key;
            mSize = size;
        }
    }

    public void clearCache() {
        
    }
}