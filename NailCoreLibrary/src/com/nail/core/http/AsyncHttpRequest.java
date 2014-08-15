package com.nail.core.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public class AsyncHttpRequest extends PriorityRunnable {

    private static final int HTTP_OK = 200;

    private HttpClient mHttpClient;
    private HttpContext mHttpContext;
    private HttpUriRequest mHttpUriRequest;
    private IHttpResult mHttpResult;
    private Class<IBaseContent> mContentCls;

    private boolean mIsCancelled = false;
    private int mRequestId = 0;

    public AsyncHttpRequest(int requestId, HttpUriRequest request, Class<IBaseContent> cls, IHttpResult result) {
        mRequestId = requestId;
        mHttpUriRequest = request;
        mHttpResult = result;
    }

    public void setClient(HttpClient client, HttpContext context) {
        mHttpClient = client;
        mHttpContext = context;
    }

    public void cancel() {
        mIsCancelled = true;
        if (mHttpUriRequest != null && !mHttpUriRequest.isAborted()) {
            mHttpUriRequest.abort();
        }
    }

    public int getRequestId() {
        return mRequestId;
    }

    @Override
    public void run() {
        if (mIsCancelled) {
            return;
        }
        try {
            HttpResponse response = mHttpClient.execute(mHttpUriRequest, mHttpContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK) {
                
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}