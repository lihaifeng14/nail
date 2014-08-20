package com.nail.core.imageloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.nail.core.http.AsyncHttpHandler;
import com.nail.core.http.IHttpHandler;
import com.nail.core.utils.CommonUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

    private static final int MEMORY_CACHE_SIZE = 1024 * 1024 * 10;
    private static final int BITMAP_MAX_PIXELS = 800*600;
    private static final int SECOND_CAPACITY = 64;

    public static final int IMAGELOADER_WORK_QUEUE_MAX_COUNT = 8;
    public static final int IMAGELOADER_THREAD_MAX_WORKER_COUNT = 5;
    public static final int IMAGELOADER_THREAD_DEF_WORDER_COUNT = 4;
    public static final int IMAGELOADER_THREAD_KEEP_ALIVE_TIME = 1;

    public static final int MSG_REQUEST = 1;
    public static final int MSG_REPLY = 2;

    private Context mContext;
    private Handler mMainHandler;
    private LruCache<String, Bitmap> mMemoryCache;
    private LinkedHashMap<String, SoftReference<Bitmap>> mSecondaryCache;
    private DiskLruCache mDiskLruCache;
    private IHttpHandler mHttpHandler;

    private Stack<ImageLoadInfo> mImageStack;

    private ImageLoaderHandler mImageLoaderHandler;

    private BlockingQueue<Runnable> mWorkQueue;
    private ThreadPoolExecutor mExecutor;

    private static ImageLoader gInstance;
    public static ImageLoader getInstance(Context context) {
        if (gInstance == null ) {
            gInstance = new ImageLoader(context);
        }
        return gInstance;
    }

    public interface BitmapLoadCallback {
        public void loadFinished(ImageView imageView, Bitmap bitmap);
    }

    public ImageLoader(Context context) {
        mContext = context;
        mMainHandler = new Handler(Looper.getMainLooper());

        mMemoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE) {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    return bitmap.getByteCount();
                } else {
                    return bitmap.getRowBytes()*bitmap.getHeight();
                }
            }
            @Override
            protected void entryRemoved(boolean evicted, String key,
                    Bitmap oldValue, Bitmap newValue) {
                if (oldValue != null) {
                    mSecondaryCache.put(key, new SoftReference<Bitmap>(oldValue));
                }
            }
        };
        mSecondaryCache = new LinkedHashMap<String, SoftReference<Bitmap>>(SECOND_CAPACITY, .75f, true) {
            private static final long serialVersionUID = 1L;
            protected boolean removeEldestEntry(
                    Entry<String, SoftReference<Bitmap>> eldest) {
                if (size() > SECOND_CAPACITY) {
                    return true;
                }
                return false;
            }
        };
        mDiskLruCache = DiskLruCache.openCache(context, -1);

        mImageStack = new Stack<ImageLoader.ImageLoadInfo>();

        mWorkQueue = new LinkedBlockingQueue<Runnable>(IMAGELOADER_WORK_QUEUE_MAX_COUNT);
        mExecutor = new ThreadPoolExecutor(
                IMAGELOADER_THREAD_DEF_WORDER_COUNT,
                IMAGELOADER_THREAD_MAX_WORKER_COUNT,
                IMAGELOADER_THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                mWorkQueue, new ThreadFactory() {
                    private final AtomicInteger mCount = new AtomicInteger(1);
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "ImageLoader" + " #" + mCount.getAndIncrement());
                    }
                }, new RejectedExecutionHandler() {
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                        Log.e("lihaifeng", "reject " + ((ImageLoadRunnable)r).mImageLoadInfo.mUrl);
                    }
                });

        HandlerThread thread = new HandlerThread("ImageLoad HandlerThread");
        thread.start();
        mImageLoaderHandler = new ImageLoaderHandler(thread.getLooper());

        mHttpHandler = new AsyncHttpHandler(mContext);
    }

    public void destory() {
        mImageStack.clear();
        mWorkQueue.clear();
        mMemoryCache.evictAll();
        mSecondaryCache.clear();
        mImageLoaderHandler.getLooper().quit();
    }

    public class ImageLoaderHandler extends Handler {
        public ImageLoaderHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case MSG_REQUEST:
                if (mWorkQueue.size() >= IMAGELOADER_WORK_QUEUE_MAX_COUNT || mImageStack.isEmpty()) {
                    return;
                }

                ImageLoadInfo info = null;
                try {
                    synchronized (mImageStack) {
                        Log.d("lihaifeng", "stack size " + mImageStack.size());
                        info = mImageStack.pop();
                    }
                } catch (EmptyStackException e) {
                    Log.e("lihaifeng", "stack null");
                }
                if (info == null) {
                    return;
                }
                Log.d("lihaifeng", "pop from stack " + info.mUrl);
                Log.d("lihaifeng", "mWorkQueue.size() " + mWorkQueue.size());
                mExecutor.execute(new ImageLoadRunnable(info));
                break;
            }
        }
    }

    public void displayImage(String url, ImageView imageView, int defaultResId, BitmapLoadCallback callback) {
        if (defaultResId > 0) {
            imageView.setImageResource(defaultResId);
        }
        Bitmap bitmap = getFromImageCache(url);
        if (bitmap != null) {
            setBitmapImage(imageView, bitmap, true, callback);
            return;
        }

        bitmap = getFromDiskCache(url);
        if (bitmap != null) {
            mMemoryCache.put(url, bitmap);
            setBitmapImage(imageView, bitmap, true, callback);
            return;
        }

        ImageLoadInfo info = new ImageLoadInfo(imageView, url, callback);
        pushToStack(info);
        sendLoadRequest();
    }

    private void sendLoadRequest() {
        Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST);
        mImageLoaderHandler.removeMessages(MSG_REQUEST);
        mImageLoaderHandler.sendMessage(message);
    }

    private void pushToStack(ImageLoadInfo info) {
        synchronized(mImageStack) {
            Iterator<ImageLoadInfo> iterator = mImageStack.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().mImageView == info.mImageView) {
                    iterator.remove();
                }
            }
            Log.d("lihaifeng", "push to stack " + info.mUrl);
            mImageStack.push(info);
        }
    }

    private void showBitmapInThread(final ImageView imageView, final Bitmap bitmap,
            final boolean showAnim, final BitmapLoadCallback callback) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                setBitmapImage(imageView, bitmap, true, callback);
            }
        });
    }

    private void setBitmapImage(ImageView imageView, Bitmap bitmap, boolean showAnim, BitmapLoadCallback callback) {
        if (callback != null) {
            callback.loadFinished(imageView, bitmap);
            imageView.setImageBitmap(bitmap);
            return;
        }

        if (showAnim) {
            TransitionDrawable td = new TransitionDrawable(
                    new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(bitmap) });
            td.setCrossFadeEnabled(true);
            imageView.setImageDrawable(td);
            td.startTransition(300);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    private Bitmap getFromImageCache(String url) {
        Bitmap bitmap = mMemoryCache.get(url);
        if (bitmap != null) {
            return bitmap;
        }

        SoftReference<Bitmap> refrence = mSecondaryCache.get(url);
        if (refrence != null) {
            bitmap = refrence.get();
            if (bitmap != null) {
                mMemoryCache.put(url, bitmap);
                mSecondaryCache.remove(url);
                return bitmap;
            } else {
                mSecondaryCache.remove(url);
            }
        }
        return null;
    }

    private void putToDiskCache(String url, Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bitmap.compress(CompressFormat.PNG, 75, bos);
            byte[] data = bos.toByteArray();
            mDiskLruCache.put(url, data);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getFromDiskCache(String url) {
        File file = mDiskLruCache.get(url);
        if (file == null) {
            return null;
        }

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 1;
        opt.inInputShareable = true;
        opt.inPurgeable = true;
        opt.inJustDecodeBounds = true;

        return BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
    }

    public static class ImageLoadInfo {
        public ImageView mImageView;
        public String mUrl;
        public BitmapLoadCallback mCallback;

        public ImageLoadInfo(ImageView imageView, String url, BitmapLoadCallback callback) {
            mImageView = imageView;
            mUrl = url;
            mCallback = callback;
        }
    }

    public class ImageLoadRunnable implements Runnable {
        private ImageLoadInfo mImageLoadInfo;
        public ImageLoadRunnable(ImageLoadInfo info) {
            mImageLoadInfo = info;
            Log.d("lihaifeng", "ImageLoadRunnable construct " + mImageLoadInfo.mUrl);
        }

        private byte[] loadByteArrayFromNetwork(String url) {
            try {
                HttpGet method = new HttpGet(url);
                HttpResponse response = mHttpHandler.excuteRequest(method);
                if (response == null) {
                    return null;
                }
                HttpEntity entity = response.getEntity();
                return EntityUtils.toByteArray(entity);
            } catch (java.net.UnknownHostException e) {
            } catch (java.net.SocketTimeoutException e) {
            } catch (Exception e) {
            } 
            return null;
        }

        @Override
        public void run() {
            Log.d("lihaifeng", "ImageLoadRunnable run " + mImageLoadInfo.mUrl);
            byte[] data = loadByteArrayFromNetwork(mImageLoadInfo.mUrl);
            if (data == null) {
                Log.e("lihaifeng", "ImageLoadRunnable failed");
                return;
            }

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inSampleSize = 1;
            opt.inInputShareable = true;
            opt.inPurgeable = true;
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, opt);
            int bitmapPixels = opt.outHeight * opt.outWidth;
            if (bitmapPixels > BITMAP_MAX_PIXELS) {
                opt.inSampleSize = CommonUtils.getSampleSize(bitmapPixels, BITMAP_MAX_PIXELS);
            }

            opt.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
                    data.length, opt);

            if (bitmap != null) {
                putToDiskCache(mImageLoadInfo.mUrl, bitmap);
                mMemoryCache.put(mImageLoadInfo.mUrl, bitmap);
                Log.e("lihaifeng", "ImageLoadRunnable finish ImageView " + mImageLoadInfo.mImageView + " " + mImageLoadInfo.mUrl);
                showBitmapInThread(mImageLoadInfo.mImageView, bitmap, true, mImageLoadInfo.mCallback);
                sendLoadRequest();
            }
        }
    }
}