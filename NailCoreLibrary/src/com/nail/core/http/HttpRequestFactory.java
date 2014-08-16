package com.nail.core.http;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

public class HttpRequestFactory {

    private static int mRequestCount = 0;

    public AsyncHttpRequest createRequest(int method, URI uri,
            List<NameValuePair> params, Class<IBaseContent> cls,
            IHttpResult result) {
        HttpUriRequest request = getUriRequest(method, uri);
        request.setParams(params);
        return null;
    }

    public AsyncHttpRequest createRequest(int method, URI uri,
            List<NameValuePair> params, InputStream inputStream,
            Class<IBaseContent> cls, IHttpResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    public AsyncHttpRequest createRequest(int method, URI uri,
            List<NameValuePair> params, File file, Class<IBaseContent> cls,
            IHttpResult result) {

    private static HttpUriRequest getUriRequest(int method, URI uri) {
        HttpUriRequest request = null;
        switch(method) {
        case IHttpHandler.HTTP_GET:
            request = new HttpGet(uri);
            break;
        case IHttpHandler.HTTP_PUT:
            request = new HttpPut(uri);
            break;
        case IHttpHandler.HTTP_POST:
            request = new HttpPost(uri);
            break;
        }
        return request;
    }
}