package com.nail.core.http;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import android.content.Context;

public class AsyncHttpHandler implements IHttpHandler {

    public static final int WORK_QUEUE_MAX_COUNT = 15;
    public static final int THREAD_MAX_WORKER_COUNT = 4;
    public static final int THREAD_DEF_WORDER_COUNT = 3;
    public static final int THREAD_KEEP_ALIVE_TIME = 1;

    private Context mContext;
    private ExecutorService mThreadPool;
    private DefaultHttpClient mHttpClient;
    private ConcurrentHashMap<Integer, AsyncHttpRequest> mRequestMap;
    private HttpContext mHttpContext;

    public AsyncHttpHandler(Context context) {
        mContext = context;
        mHttpClient = AsyncHttpClient.createAsyncHttpClient();

        mThreadPool = new ThreadPoolExecutor(THREAD_DEF_WORDER_COUNT, THREAD_MAX_WORKER_COUNT, 
                THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(WORK_QUEUE_MAX_COUNT),
                new ThreadFactory() {
                    private final AtomicInteger mThreadCount = new AtomicInteger(1);
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "AsyncHttpHandler Thread #" + mThreadCount.getAndIncrement());
                    }
                }, new RejectedExecutionHandler() {
                    public void rejectedExecution(Runnable r,
                            ThreadPoolExecutor e) {
                    }
                });

        mHttpContext = new SyncBasicHttpContext(new BasicHttpContext());


        mRequestMap = new ConcurrentHashMap<Integer, AsyncHttpRequest>();
    }

    private void updateHttpContext() {
        mHttpContext.setAttribute(ClientContext.AUTHSCHEME_REGISTRY,
              mHttpClient.getAuthSchemes());
        mHttpContext.setAttribute(ClientContext.COOKIESPEC_REGISTRY,
              mHttpClient.getCookieSpecs());
        mHttpContext.setAttribute(ClientContext.COOKIE_STORE,
              mHttpClient.getCookieStore());
        mHttpContext.setAttribute(ClientContext.CREDS_PROVIDER,
              mHttpClient.getCredentialsProvider());
    }

    public void setUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(mHttpClient.getParams(), userAgent);
    }

    @Override
    public void sendRequest(AsyncHttpRequest request) {
        updateHttpContext();
        request.setClient(mHttpClient, mHttpContext);
        mThreadPool.submit(request);
    }

    @Override
    public void cancelRequest(int id) {
        AsyncHttpRequest request = mRequestMap.get(id);
        if (request != null) {
            request.cancel();
            mRequestMap.remove(id);
        }
    }

    @Override
    public void cancelRequests() {
        for (Entry<Integer, AsyncHttpRequest> entry : mRequestMap.entrySet()) {
            entry.getValue().cancel();
        }
        mRequestMap.clear();
    }
}