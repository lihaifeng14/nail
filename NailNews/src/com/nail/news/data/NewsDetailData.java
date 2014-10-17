package com.nail.news.data;

import com.nail.core.http.IBaseContent;

public class NewsDetailData implements IBaseContent {
    private NewsData data;

    public final NewsData getData() {
        return data;
    }

    public final void setData(NewsData data) {
        this.data = data;
    }

    public static class NewsData implements IBaseContent {
        private NewsMetaData meta;
        private NewsItemData body;

        public final NewsMetaData getMeta() {
            return meta;
        }
        public final void setMeta(NewsMetaData meta) {
            this.meta = meta;
        }
        public final NewsItemData getBody() {
            return body;
        }
        public final void setBody(NewsItemData body) {
            this.body = body;
        }
    }
}