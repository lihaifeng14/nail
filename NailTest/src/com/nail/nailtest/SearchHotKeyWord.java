package com.nail.nailtest;

import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class SearchHotKeyWord implements IBaseContent {
    private String code;
    private String desc;
    private ArrayList<SearchHotKeyWordItem> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<SearchHotKeyWordItem> getData() {
        return data;
    }

    public void setData(ArrayList<SearchHotKeyWordItem> data) {
        this.data = data;
    }

    public static class SearchHotKeyWordItem implements IBaseContent {
        private String title;
    
        public String getTitle() {
            return title;
        }
    
        public void setTitle(String title) {
            this.title = title;
        }
    }
}