package com.nail.news.data;

import com.nail.core.http.IBaseContent;

public class NewsMetaData implements IBaseContent {

    private String id; // "http://api.3g.ifeng.com/iosNews?id=QC45,FOCUSQC45"
    private String documentId; // "QC45"
    private String type; // "list"
    private long expiredTime; // 180000
    private int pageSize; // 5

    public final String getId() {
        return id;
    }
    public final void setId(String id) {
        this.id = id;
    }
    public final String getDocumentId() {
        return documentId;
    }
    public final void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public final String getType() {
        return type;
    }
    public final void setType(String type) {
        this.type = type;
    }
    public final long getExpiredTime() {
        return expiredTime;
    }
    public final void setExpiredTime(long expiredTime) {
        this.expiredTime = expiredTime;
    }
    public final int getPageSize() {
        return pageSize;
    }
    public final void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}