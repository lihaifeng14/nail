package com.nail.core.http;

import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;

public class HttpRequestFactory {

    private static int mRequestCount = 0;

    AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params, 
            Class<IBaseContent> cls, IHttpResult result) {
        return null;
    }
}