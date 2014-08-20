package com.nail.core.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
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

    @Override
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

    @Override
    public AsyncHttpRequest creatGetRequest(URI uri, Class<? extends IBaseContent> cls,
            IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_GET, uri, cls, result);
    }

    @Override
    public AsyncHttpRequest creatDelRequest(URI uri, Class<? extends IBaseContent> cls,
            IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_DELETE, uri, cls, result);
    }

    @Override
    public AsyncHttpRequest createPutRequest(URI uri,
            List<NameValuePair> params, Class<? extends IBaseContent> cls,
            IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_PUT, uri, params, cls, result);
    }

    @Override
    public AsyncHttpRequest createPutRequest(URI uri, InputStream inputStream,
            Class<? extends IBaseContent> cls, IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_PUT, uri, null, null, inputStream, cls, result);
    }

    @Override
    public AsyncHttpRequest createPutRequest(URI uri, File file, Class<? extends IBaseContent> cls, IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_PUT, uri, null, null, file, cls, result);
    }

    @Override
    public AsyncHttpRequest createPostRequest(URI uri,
            List<NameValuePair> params, Class<? extends IBaseContent> cls, IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_POST, uri, params, cls, result);
    }

    @Override
    public AsyncHttpRequest createPostRequest(URI uri, List<NameValuePair> params, String keyName,
            InputStream inputStream, Class<? extends IBaseContent> cls, IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_POST, uri, params, keyName, inputStream, cls, result);
    }

    @Override
    public AsyncHttpRequest createPostRequest(URI uri,
            List<NameValuePair> params, String keyName, File file, Class<? extends IBaseContent> cls, IHttpResult result) {
        return HttpRequestFactory.createRequest(HTTP_POST, uri, params, keyName, file, cls, result);
    }

    @Override
    public HttpResponse excuteRequest(HttpUriRequest request) {
        HttpResponse response = null;
        try {
            response = mHttpClient.execute(request);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}