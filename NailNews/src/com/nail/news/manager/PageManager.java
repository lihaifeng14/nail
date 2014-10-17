package com.nail.news.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nail.news.data.PageContent;

public class PageManager {

    private List<Integer> mListCurTypes;
    private Map<Integer, PageContent> mMapPages;

    protected static PageManager mInstance;
    public static PageManager getInstance() {
        if (mInstance == null) {
            mInstance = new PageManager();
            mInstance.init();
        }
        return mInstance;
    }

    public void init() {
        mMapPages = new HashMap<Integer, PageContent>();
        mMapPages.put(PageContent.PAGE_TOUTIAO, new PageContent(PageContent.PAGE_TOUTIAO, "头条", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_YULE, new PageContent(PageContent.PAGE_YULE, "娱乐", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_TIYU, new PageContent(PageContent.PAGE_TIYU, "体育", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_CAIJING, new PageContent(PageContent.PAGE_CAIJING, "财经", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_QICHE, new PageContent(PageContent.PAGE_QICHE, "汽车", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_KEJI, new PageContent(PageContent.PAGE_KEJI, "科技", PageContent.PAGE_TYPE_PICANDNEWS));
        mMapPages.put(PageContent.PAGE_ZIMEITI, new PageContent(PageContent.PAGE_ZIMEITI, "自媒体", PageContent.PAGE_TYPE_NEWS));

        mListCurTypes = new ArrayList<Integer>();
        mListCurTypes.add(PageContent.PAGE_TOUTIAO);
        mListCurTypes.add(PageContent.PAGE_YULE);
        mListCurTypes.add(PageContent.PAGE_TIYU);
        mListCurTypes.add(PageContent.PAGE_CAIJING);
        mListCurTypes.add(PageContent.PAGE_QICHE);
        mListCurTypes.add(PageContent.PAGE_KEJI);
        mListCurTypes.add(PageContent.PAGE_ZIMEITI);
    }

    public List<Integer> getTypes() {
        return mListCurTypes;
    }

    public PageContent getPageByIndex(int index) {
        if (mListCurTypes == null || index < 0 || index >= mListCurTypes.size()) {
            return null;
        }
        return getPageByType(mListCurTypes.get(index));
    }

    public PageContent getPageByType(int type) {
        return mMapPages.get(type);
    }
}