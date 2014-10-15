package com.nail.news;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

public class NailNewsApplication extends Application {
    private  static NailNewsApplication mInstance;

    public static NailNewsApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String processName = getProcessName(this);
        String packageName = getPackageName();
        if (packageName.equals(processName)) {
            mInstance = this;
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