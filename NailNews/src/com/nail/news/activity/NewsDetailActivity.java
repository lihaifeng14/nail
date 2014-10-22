package com.nail.news.activity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.nail.core.imageloader.ImageLoader;
import com.nail.news.R;
import com.nail.news.data.NewsDetailData;
import com.nail.news.data.NewsItemData;
import com.nail.news.data.NewsItemData.ImageData;
import com.nail.news.manager.NewsDetailManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class NewsDetailActivity extends Activity implements
        ImageLoader.BitmapDownLoadCallback, NewsDetailManager.NewDetailCallback, View.OnClickListener {

    public static final String IMG_LOCAL_SRC = "file:///android_asset/image/image_default.png";

    public static final String EXTRA_DOCUMENT_ID = "extra_document_id";
    public static final String EXTRA_COMMENTS_COUNT= "extra_comments_count";

    private NewsDetailManager mManager;
    private String mDocumentId;
    private String mCommentUrl;

    private WebView mWebView;
    private WebSettings mWebSettings;
    private ImageLoader mImageLoader;

    private List<String> mListImageUrls;
    private List<String> mListBigImageUrls;

    private View mBackView;
    private View mCommentsView;
    private TextView mComments;
    private View mDetailLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsdetail);

        mImageLoader = ImageLoader.getInstance(getApplicationContext());
        mManager = NewsDetailManager.getInstance();

        mWebView = (WebView) findViewById(R.id.newsdetail_web);
        mBackView = findViewById(R.id.button_back);
        mBackView.setOnClickListener(this);
        mCommentsView = findViewById(R.id.button_comments);
        mComments = (TextView)findViewById(R.id.text_comments);
        mComments.setText(getIntent().getIntExtra(EXTRA_COMMENTS_COUNT, 0)+"评论");
        mCommentsView.setOnClickListener(this);

        mDetailLoading = findViewById(R.id.detail_loading);

        mWebSettings = mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setDefaultTextEncodingName("UTF-8");

        mListImageUrls = new ArrayList<String>();

        // HtmlParserTask task = new HtmlParserTask(mWebView, this, "测试测试",
        // "你好");
        // task.execute();

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                int size = mListImageUrls.size();
                for (int i = 0; i < size; i++) {
                    String index = String.valueOf(i);
                    mImageLoader.downloadImage(mListImageUrls.get(i), index,
                            NewsDetailActivity.this);
                }
            }
        });
        mDocumentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);
        mManager.getDetailData(mDocumentId, this);
    }

    class HtmlParserTask extends AsyncTask<Void, Void, String> {

        private static final String TAG = "HtmlParser";

        private String mTitle;
        private String mContent;
        private WebView mWebView;
        private Context mContext;

        public HtmlParserTask(WebView wevView, Context context, String title,
                String content) {
            mWebView = wevView;
            mContext = context;
            mTitle = title;
            mContent = content;
        }

        @Override
        protected String doInBackground(Void... params) {
            mListImageUrls.clear();

            String html;
            try {
                // InputStream inputStream =
                // getResources().openRawResource(R.raw.content);
                // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // byte[] buf= new byte[4096];
                // int bytes = 0;
                // while ((bytes = inputStream.read(buf)) != -1) {
                // bos.write(buf, 0, bytes);
                // }

                html = readHtmlTemplateFile();
                long t1 = System.currentTimeMillis();
                Document doc = Jsoup.parse(html);
                if (doc == null) {
                    return null;
                }
                // 替换标题
                Element element = doc.getElementById("title");
                element.html(mTitle);
                // 替换body
                element = doc.getElementById("content");
                // String body = bos.toString();
                element.html(mContent);

                // 替换正文后重新解析
                doc = Jsoup.parse(doc.html());

                // 解析图片标签
                handleImageClickEvent(doc);
                // if (removeAutoLink) {
                // removeHyperlinks(doc);
                // }

                html = doc.outerHtml();
                return html;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            mDetailLoading.setVisibility(View.GONE);
            // 加载正文
            if (!TextUtils.isEmpty(result) && mWebView != null) {
                mWebView.loadDataWithBaseURL(null, result, "text/html",
                        "utf-8", null);
            }
        }
    }

    private void handleImageClickEvent(Document doc) throws IOException {
        Elements es = doc.getElementsByTag("img");
        int size = es.size();
        boolean couldReplace = mListBigImageUrls != null
                && mListBigImageUrls.size() == size;

        File imgFile;
        String localImgPath;
        for (int i = 0; i < size; i++) {
            Element e = es.get(i);
            // String type = e.attr("type");
            // if(type.equals("image")) {
            // 取得图片的真实地址
            String imgUrl = e.attr("src");
            if (imgUrl.startsWith("file")) { // 来自assets文件夹的图片,如视频背景图
                e.attr("src", imgUrl);
                e.attr("ori_link", imgUrl);
            } else {
                e.attr("id", "img_" + mListImageUrls.size());
                // 替换img url
                if (couldReplace) {
                    imgUrl = mListBigImageUrls.get(mListImageUrls.size());
                    e.attr("width", "500");
                }
                mListImageUrls.add(imgUrl);
                e.attr("src", IMG_LOCAL_SRC);
                e.attr("ori_link", imgUrl);

                // 图片点击 跳新的页面
                // String click = "window." + JAVASCRIPT_NAME +
                // ".showImageInActivity('"
                // + index + "')";
                // e.attr("onclick", click);
            }
            // }
        }
    }

    public String readHtmlTemplateFile() throws IOException {

        StringBuffer strBuffer = null;
        File file;
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        try {
            inputStream = getAssets().open("template.html");
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);
            // 读取一行
            String line = null;
            strBuffer = new StringBuffer();

            while ((line = bufferReader.readLine()) != null) {
                strBuffer.append(line);
            }
            return strBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (inputReader != null)
                    inputReader.close();
                if (bufferReader != null)
                    bufferReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDownLoadComplete(String tag, String url) {
        File file = mImageLoader.getImageFile(url);
        String js = "javascript:(function(){"
                + "var imgEle = document.getElementById(\"img_" + tag
                + "\"); "
                + "var imgOriUrl = imgEle.getAttribute(\"ori_link\"); "
                + // 真实地址
                "if(imgOriUrl == \"" + url + "\") {"
                + "   imgEle.setAttribute(\"src\", \""
                + Uri.fromFile(file).toString() + "\");" + "}" + "})()";
        mWebView.loadUrl(js);
    }

    @Override
    public void onDetailCallback(NewsDetailData data) {
        if (data.getData() == null) {
            return;
        }

        NewsItemData item = data.getData().getBody();
        String title = item.getTitle();
        String content = item.getText();
        mCommentUrl = item.getCommentsUrl();
        List<ImageData> imgs = item.getImg();
        if (imgs != null) {
            mListBigImageUrls = new ArrayList<String>();
            for (ImageData img : imgs) {
                mListBigImageUrls.add(img.getUrl());
            }
        }

        HtmlParserTask task = new HtmlParserTask(mWebView, this, title, content);
        task.execute();
    }

    @Override
    public void onDetailFailed() {
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.button_back:
            finish();
            break;
        case R.id.button_comments:
            if (mCommentUrl == null) {
                return;
            }
            Intent intent = new Intent(this, CommentsActivity.class);
            intent.putExtra(CommentsActivity.EXTRA_COMMENTS_URL, mCommentUrl);
            startActivity(intent);
        }
    }
}