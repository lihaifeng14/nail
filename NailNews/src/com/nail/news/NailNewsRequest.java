package com.nail.news;

import com.nail.news.data.PageContent;

public class NailNewsRequest {

    private static final String FORMAT_TAG_FIRST = "%1$s";
    private static final String FORMAT_TAG_SECOND = "%2$s";
    private static final String FORMAT_TAG_THIRD = "%3$s";

    public static final String IFENG_SPLASH_URL = "http://api.iapps.ifeng.com/news/cover_new.json?";

    public static final String IFENG_HOST_URL = "http://api.3g.ifeng.com/";
    public static final String IFENG_NEWS_URL_TOUTIAO = IFENG_HOST_URL + 
            "iosNews?id=SYLB10,SYDT10" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_YULE = IFENG_HOST_URL + 
            "iosNews?id=YL53,FOCUSYL53" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_TIYU = IFENG_HOST_URL + 
            "iosNews?id=TY43,FOCUSTY43" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_CAIJING = IFENG_HOST_URL + 
            "iosNews?id=CJ33,FOCUSCJ33" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_QICHE = IFENG_HOST_URL + 
            "iosNews?id=QC45,FOCUSQC45" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_KEJI = IFENG_HOST_URL + 
            "iosNews?id=KJ123,FOCUSKJ123" + "&page=" + FORMAT_TAG_FIRST;
    public static final String IFENG_NEWS_URL_ZMT = IFENG_HOST_URL + 
            "iosNews?id=ZMT10" + "&page=" + FORMAT_TAG_FIRST;

    public static final String IFENG_NEWS_DETAIL_URL = IFENG_HOST_URL + 
            "ipadtestdoc?imgwidth=100&aid=" + FORMAT_TAG_FIRST;

    public static final String IFENG_NEWS_COMMENTS_URL = "http://icomment.ifeng.com/geti.php?pagesize=" + 
            FORMAT_TAG_FIRST + "&p=" + FORMAT_TAG_SECOND + "&docurl=" + FORMAT_TAG_THIRD + "&type=all";

    public static NailNewsRequest mInstance;
    public static NailNewsRequest getInstance() {
        if (mInstance == null) {
            mInstance = new NailNewsRequest();
        }
        return mInstance;
    }

    public String getSplashUrl() {
        return IFENG_SPLASH_URL;
    }

    public String getDetailUrl(String documentId) {
        return String.format(NailNewsRequest.IFENG_NEWS_DETAIL_URL, documentId);
    }

    public String getNewsUrl(int type, int page) {
        switch (type) {
        case PageContent.PAGE_TOUTIAO:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_TOUTIAO, page);
        case PageContent.PAGE_YULE:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_YULE, page);
        case PageContent.PAGE_TIYU:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_TIYU, page);
        case PageContent.PAGE_CAIJING:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_CAIJING, page);
        case PageContent.PAGE_QICHE:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_QICHE, page);
        case PageContent.PAGE_KEJI:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_KEJI, page);
        case PageContent.PAGE_ZIMEITI:
            return String.format(NailNewsRequest.IFENG_NEWS_URL_ZMT, page);
        }
        return null;
    }

    private static final int COMMENTS_PAGE_SIZE = 20;
    public String getCommentsUrl(String url, int page) {
        return String.format(IFENG_NEWS_COMMENTS_URL, COMMENTS_PAGE_SIZE, page, url);
    }
}