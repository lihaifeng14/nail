package com.nail.core.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.text.TextUtils;

public class AsyncHttpRequest extends PriorityRunnable {

    private static final int HTTP_OK = 200;

    private HttpClient mHttpClient;
    private HttpContext mHttpContext;
    private IHttpResult mHttpResult;
    private Class<? extends IBaseContent> mContentCls;

    protected HttpUriRequest mHttpUriRequest;
    protected int mMethod;
    protected List<NameValuePair> mParams;

    private boolean mIsCancelled = false;
    private int mRequestId = 0;

    public AsyncHttpRequest(int method, int requestId, List<NameValuePair> params, 
            HttpUriRequest request, Class<? extends IBaseContent> cls, IHttpResult result) {
        mMethod = method;
        mRequestId = requestId;
        mParams = params;
        mHttpUriRequest = request;
        mContentCls = cls;
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

    public void pretreatRequest() {
        if (mParams == null) {
            return;
        }

        try {
            if (mMethod == IHttpHandler.HTTP_PUT) {
                HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase)mHttpUriRequest;
                request.setEntity(new UrlEncodedFormEntity(mParams, HTTP.UTF_8));
            } else {
                HttpEntityEnclosingRequestBase request = (HttpEntityEnclosingRequestBase)mHttpUriRequest;
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                for (NameValuePair nameValuePair : mParams) {
                    reqEntity.addPart(nameValuePair.getName(), new StringBody(nameValuePair.getValue()));
                }
                request.setEntity(reqEntity);
            }
        } catch (UnsupportedEncodingException e) {
        }
    }

    @Override
    public void run() {
        if (mIsCancelled) {
            return;
        }
        try {
            // 设置request的entity
            pretreatRequest();

            HttpResponse response = mHttpClient.execute(mHttpUriRequest, mHttpContext);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HTTP_OK) {
                HttpEntity entity = response.getEntity();
                Header contentType = entity.getContentType();
                String charset = getCharset(contentType.toString());
                String content =  EntityUtils.toString(entity, charset);

                IBaseContent result = null;
                JSONObject json = JSON.parseObject(content);
                if (json != null) {
                    result = JSON.toJavaObject(json, mContentCls);
                }
                entity.consumeContent();
                if (mHttpResult != null) {
                    mHttpResult.onRequestSuccess(mRequestId, result);
                }
            } else {
                HttpException exception = new HttpException(statusCode);
                HttpEntity entity = response.getEntity();
                Header contentType = entity.getContentType();
                if (contentType != null && isJson(contentType.toString())) {
                    String charset = getCharset(entity.getContentType().toString());
                    String content =  EntityUtils.toString(entity, charset);
                    exception.setErrorJson(content);
                }
                entity.consumeContent();
                if (mHttpResult != null) {
                    mHttpResult.onRequestFailed(mRequestId, exception);
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        } catch (ConnectException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        } catch (SocketException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        } catch (IOException e) {
            e.printStackTrace();
            mHttpResult.onRequestFailed(mRequestId, new HttpException(e));
        }
    }

    private final static String CHARSET = "charset=";
    private final static String CONTENT_TYPE_JSON = "application/json";

    public static String getCharset(String contentType) {
        if (TextUtils.isEmpty(contentType)) {
            return HTTP.UTF_8;
        }
        int index = contentType.indexOf(CHARSET);
        if (index >= 0) {
            String charset = contentType.substring(index + CHARSET.length()).trim();
            if (charset.length() > 0) {
                return charset;
            }
        }
        return HTTP.UTF_8;
    }

    public static boolean isJson(String contentType) {
        if (TextUtils.isEmpty(contentType)) {
            return false;
        }
        int index = contentType.indexOf(CONTENT_TYPE_JSON);
        if (index > 0) {
            return true;
        }
        return false;
    }
}