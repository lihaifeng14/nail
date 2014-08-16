package com.nail.core.http;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;

public interface IHttpHandler {

    public static final int HTTP_GET = 0;
    public static final int HTTP_PUT = 1;
    public static final int HTTP_POST = 2;

    public AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params, 
            Class<IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params, 
            InputStream inputStream, Class<IBaseContent> cls, IHttpResult result);

    public AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params, 
            File file, Class<IBaseContent> cls, IHttpResult result);

    public void sendRequest(AsyncHttpRequest request);
    public void cancelRequest(int id);
    public void cancelRequests();
}