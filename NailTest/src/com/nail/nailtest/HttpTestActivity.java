package com.nail.nailtest;

import java.net.URI;
import java.net.URISyntaxException;

import com.nail.core.http.AsyncHttpHandler;
import com.nail.core.http.AsyncHttpRequest;
import com.nail.core.http.HttpException;
import com.nail.core.http.IBaseContent;
import com.nail.core.http.IHttpHandler;
import com.nail.core.http.IHttpResult;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.view.View;

public class HttpTestActivity extends Activity implements View.OnClickListener, IHttpResult{
    private IHttpHandler mHttpHandler;

    public static final String HTTP_DATA_HOST = "http://api.video.sina.com.cn/sourceProgram/phone";
    public static final String HTTP_GET_HOT = HTTP_DATA_HOST + "/getSearchTop.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httptest);
        Button btn1 = (Button)findViewById(R.id.button_1);
        btn1.setOnClickListener(this);
        mHttpHandler = new AsyncHttpHandler(this);
        mHttpHandler.setUserAgent("MyUA");
    }

    @Override
    public void onClick(View v) {
        try {
            AsyncHttpRequest request = mHttpHandler.creatGetRequest(new URI(HTTP_GET_HOT), SearchHotKeyWord.class, this);
            mHttpHandler.sendRequest(request);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestSuccess(int id, IBaseContent content) {
        Log.d("lihaifeng", "Result OK " + content);
    }

    @Override
    public void onRequestFailed(int id, HttpException e) {
        Log.d("lihaifeng", "Result Failed " + e);
    }
}