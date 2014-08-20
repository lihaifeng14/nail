package com.nail.core.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class StorageUtils {

    public static File getCacheDirectory(Context context, boolean preferExternal, String directory) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) {
            externalStorageState = "";
        }

        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState)) {
            appCacheDir = context.getExternalFilesDir(directory);
        }

        if (appCacheDir == null) {
            if (context.getCacheDir() != null) {
                String cacheDirPath = context.getCacheDir().getPath() + File.separator + directory;
                appCacheDir = new File(cacheDirPath);
            } else {
                String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
                appCacheDir = new File(cacheDirPath);
            }
        }

        return appCacheDir;
    }
}