package com.nail.core.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class HttpRequestFactory {

    private static int mRequestCount = 0;

    public static AsyncHttpRequest createRequest(int method, URI uri, Class<? extends IBaseContent> cls,
            IHttpResult result) {
        return createRequest(method, uri, null, cls, result);
    }

    public static AsyncHttpRequest createRequest(int method, URI uri, 
            List<NameValuePair> params, Class<? extends IBaseContent> cls, IHttpResult result) {
        HttpUriRequest request = getUriRequest(method, uri);
        return new AsyncHttpRequest(method, ++mRequestCount, params, request, cls, result);
    }

    public static AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params, 
            String keyName, InputStream inputStream, Class<? extends IBaseContent> cls, IHttpResult result) {
        HttpUriRequest request = getUriRequest(method, uri);
        return new InputStreamHttpRequest(method, ++mRequestCount, params,
                keyName, inputStream, request, cls, result);
    }

    public static AsyncHttpRequest createRequest(int method, URI uri, List<NameValuePair> params,
            String keyName, File file, Class<? extends IBaseContent> cls, IHttpResult result) {
        HttpUriRequest request = getUriRequest(method, uri);
        return new FileHttpRequest(method, ++mRequestCount, params,
                keyName, file, request, cls, result);
    }

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

    public static class FileHttpRequest extends AsyncHttpRequest {

        private File mFile;
        private String mKeyName;

        public FileHttpRequest(int method, int requestId, List<NameValuePair> params, 
                String keyName, File file, HttpUriRequest request, Class<? extends IBaseContent> cls, IHttpResult result) {
            super(method, requestId, params, request, cls, result);
            mFile = file;
            mKeyName = keyName;
        }

        @Override
        public void pretreatRequest() {
            if (mFile == null) {
                return;
            }

            HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase)mHttpUriRequest;
            if (mMethod == IHttpHandler.HTTP_PUT) {
                request.setEntity(new FileEntity(mFile, "application/octet-stream"));
            } else {
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                if (mParams != null) {
                    try {
                        for (NameValuePair nameValuePair : mParams) {
                            entity.addPart(nameValuePair.getName(), new StringBody(nameValuePair.getValue()));
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                entity.addPart(mKeyName, new FileBody(mFile, "application/octet-stream"));
                request.setEntity(entity);
            }
        }
    }

    public static class InputStreamHttpRequest extends AsyncHttpRequest {

        private InputStream mInputStream;
        private String mKeyName;

        public InputStreamHttpRequest(int method, int requestId, List<NameValuePair> params,
                String keyName, InputStream inputStream, HttpUriRequest request, Class<? extends IBaseContent> cls, IHttpResult result) {
            super(method, requestId, params, request, cls, result);
            mInputStream = inputStream;
            mKeyName = keyName;
        }

        @Override
        public void pretreatRequest() {
            if (mInputStream == null) {
                return;
            }

            ByteArrayOutputStream bos = null;
            byte[] buffer = null;
            try {
                bos = new ByteArrayOutputStream();
                buffer = new byte[1024];
                int count;

                while ((count = mInputStream.read(buffer)) > 0) {
                    bos.write(buffer, 0, count);
                }
                HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase)mHttpUriRequest;
                if (mMethod == IHttpHandler.HTTP_PUT) {
                    request.setEntity(new ByteArrayEntity(bos.toByteArray()));
                } else {
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                    if (mParams != null) {
                        try {
                            for (NameValuePair nameValuePair : mParams) {
                                entity.addPart(nameValuePair.getName(), new StringBody(nameValuePair.getValue()));
                            }
                        } catch (UnsupportedEncodingException e) {
                        }
                    }
                    entity.addPart(mKeyName, new ByteArrayBody(bos.toByteArray(), ""));
                    request.setEntity(entity);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bos = null;
                }
                buffer = null;
            }
        }
    }
}