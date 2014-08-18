package com.nail.core.http;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;

public interface IHttpHandler {

    public static final int HTTP_GET    = 0;
    public static final int HTTP_PUT    = 1;
    public static final int HTTP_POST   = 2;
    public static final int HTTP_DELETE = 3;

    public AsyncHttpRequest creatGetRequest(URI uri, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest creatDelRequest(URI uri, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPutRequest(URI uri, List<NameValuePair> params, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPutRequest(URI uri, InputStream inputStream, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPutRequest(URI uri, File file, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPostRequest(URI uri, List<NameValuePair> params, 
            Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPostRequest(URI uri, List<NameValuePair> params, 
            String keyName, InputStream inputStream, Class<? extends IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createPostRequest(URI uri, List<NameValuePair> params, 
            String keyName, File file, Class<? extends IBaseContent> cls, IHttpResult result);

    public void setUserAgent(String userAgent);
    public void sendRequest(AsyncHttpRequest request);
    public void cancelRequest(int id);
    public void cancelRequests();
}