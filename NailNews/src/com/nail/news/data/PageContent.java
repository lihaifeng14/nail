package com.nail.news.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.nail.core.http.IBaseContent;
import com.nail.news.NailNewsRequest;

public class PageContent {

    public static final long REFRESH_INTEVAL_TIME = 2 * 60 * 1000;

    public static final int PAGE_TYPE_PICANDNEWS = 0;
    public static final int PAGE_TYPE_NEWS = 1;

    public static final int PAGE_TOUTIAO = 0;
    public static final int PAGE_YULE = 1;
    public static final int PAGE_TIYU = 2;
    public static final int PAGE_CAIJING = 3;
    public static final int PAGE_QICHE = 4;
    public static final int PAGE_KEJI = 5;
    public static final int PAGE_ZIMEITI = 6;

    public interface NewsDataListener {

        public void onNewsData(int type, PageContent content);

        public void onNewsFailed(int type);

        public void onNoMoreData(int type);

        public void onNoNewData(int type);
    }

    public int mPageType;
    public String mTitle;
    public int mType;
    public int mPageIndex; // 0表示没有任何内容

    public SigTypeNewsData mFocusData;
    public List<NewsItemData> mNewsData;

    public Set<NewsDataListener> mListeners;

    private long mFetchTime;

    public PageContent(int type, String title, int pageType) {
        mType = type;
        mTitle = title;
        mPageType = pageType;
        mPageIndex = 0;
        mListeners = new HashSet<PageContent.NewsDataListener>();
        mFetchTime = 0;
    }

    public String getFirstRequestUrl() {
        mPageIndex = 0;
        return getRequestUrl();
    }

    public String getRequestUrl() {
        int page = mPageIndex + 1;
        return NailNewsRequest.getInstance().getNewsUrl(mType, page);
    }

    public boolean setFirstData(IBaseContent content, boolean fetch) {
        if (fetch) {
            mFetchTime = System.currentTimeMillis();
        }

        if (content == null || !(content instanceof PicNewsData)) {
            return false;
        }

        PicNewsData data = (PicNewsData) content;
        ArrayList<SigTypeNewsData> list = data.getData();
        if (mPageType == PAGE_TYPE_PICANDNEWS && list.size() < 2) {
            return false;
        }

        if (isSameData(data)) {
            return false;
        }

        SigTypeNewsData sigNews = list.get(0);
        if (mPageType == PAGE_TYPE_PICANDNEWS) {
            mFocusData = list.get(1);
        }
        mNewsData = new ArrayList<NewsItemData>();
        mNewsData.addAll(sigNews.getBody().getItem());
        mPageIndex = 1;

        return true;
    }

    private boolean isSameData(PicNewsData data) {
        if (mPageType == PAGE_TYPE_PICANDNEWS) {
            if (mFocusData == null) {
                return false;
            }
            ArrayList<NewsItemData> listOld = mFocusData.getBody().getItem();
            ArrayList<NewsItemData> listNew = data.getData().get(1).getBody()
                    .getItem();
            if (listOld.size() != listNew.size()) {
                return false;
            }
            for (int i = 0; i < listOld.size(); i++) {
                NewsItemData itemOld = listOld.get(0);
                NewsItemData itemNew = listNew.get(0);
                if (!isSameItem(itemOld, itemNew)) {
                    return false;
                }
            }
        }

        if (mNewsData == null) {
            return false;
        }
        ArrayList<NewsItemData> newsList = data.getData().get(0).getBody()
                .getItem();
        if (mNewsData.size() < newsList.size()) {
            return false;
        }
        for (int i = 0; i < newsList.size(); i++) {
            NewsItemData itemOld = mNewsData.get(0);
            NewsItemData itemNew = newsList.get(0);
            if (!isSameItem(itemOld, itemNew)) {
                return false;
            }
        }
        return true;
    }

    private boolean isSameItem(NewsItemData itemOld, NewsItemData itemNew) {
        if (itemNew.getTitle().compareTo(itemOld.getTitle()) == 0
                && itemNew.getThumbnail().compareTo(itemOld.getThumbnail()) == 0
                && itemNew.getComments() == itemOld.getComments()) {
            return true;
        }
        return false;
    }

    public boolean addData(IBaseContent content) {
        if (content == null || !(content instanceof PicNewsData)) {
            return false;
        }
        if (mNewsData == null) {
            return false;
        }

        PicNewsData data = (PicNewsData) content;
        ArrayList<SigTypeNewsData> list = data.getData();
        if (list.size() == 0) {
            return false;
        }

        SigTypeNewsData sigNews = list.get(0);
        mNewsData.addAll(sigNews.getBody().getItem());
        mPageIndex += 1;
        return true;
    }

    public void setDataFailed() {

    }

    public boolean needRefreshData() {
        long now = System.currentTimeMillis();
        long inteval = now - mFetchTime;
        if (inteval > REFRESH_INTEVAL_TIME) {
            return true;
        }
        return false;
    }

    public boolean hasFocusData() {
        try {
            if (mPageType == PAGE_TYPE_PICANDNEWS && mFocusData != null
                    && mFocusData.getBody().getItem().size() > 0) {
                return true;
            }
        } catch (NullPointerException e) {

        }
        return false;
    }

    public int getItemCount() {
        int itemCount = 0;
        if (hasFocusData()) {
            itemCount++;
        }
        if (mNewsData != null) {
            itemCount += mNewsData.size();
        }
        return itemCount;
    }

    public void addListener(NewsDataListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(NewsDataListener listener) {
        mListeners.remove(listener);
    }

    public void notifyNewData() {
        for (NewsDataListener listener : mListeners) {
            listener.onNewsData(mType, this);
        }
    }

    public void notifyNoNewData() {
        for (NewsDataListener listener : mListeners) {
            listener.onNoNewData(mType);
        }
    }

    public void notifyNoMoreData() {
        for (NewsDataListener listener : mListeners) {
            listener.onNoMoreData(mType);
        }
    }

    public void notifyFailed() {
        for (NewsDataListener listener : mListeners) {
            listener.onNewsFailed(mType);
        }
    }
}