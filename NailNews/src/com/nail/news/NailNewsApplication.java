package com.nail.news;

import com.nail.core.http.AsyncHttpHandler;
import com.nail.news.manager.CacheManager;
import com.nail.news.manager.NewsDetailManager;
import com.nail.news.manager.NewsFragmentManager;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

public class NailNewsApplication extends Application {

    private static NailNewsApplication mInstance;
    public AsyncHttpHandler mHttpHandler;

    public static NailNewsApplication getInstance() {
        return mInstance;
    }

    public AsyncHttpHandler getHttpHandler() {
        return mHttpHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String processName = getProcessName(this);
        String packageName = getPackageName();
        if (packageName.equals(processName)) {
            mInstance = this;
            mHttpHandler = new AsyncHttpHandler(this);
            CacheManager.getInstance().init(this);
            NewsFragmentManager.getInstance().init(this);
            NewsDetailManager.getInstance().init(this);
        }
    }

    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}