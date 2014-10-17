package com.nail.news.data;

import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class SigTypeNewsData implements IBaseContent{

    NewsMetaData meta;
    NewBodyData body;

    public final NewsMetaData getMeta() {
        return meta;
    }
    public final void setMeta(NewsMetaData meta) {
        this.meta = meta;
    }

    public final NewBodyData getBody() {
        return body;
    }
    public final void setBody(NewBodyData body) {
        this.body = body;
    }

    public static class NewBodyData implements IBaseContent {
        ArrayList<NewsItemData> item;

        public final ArrayList<NewsItemData> getItem() {
            return item;
        }

        public final void setItem(ArrayList<NewsItemData> item) {
            this.item = item;
        }
    }
}
