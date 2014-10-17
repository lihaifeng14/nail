package com.nail.news.data;
import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class PicNewsData implements IBaseContent {
    private ArrayList<SigTypeNewsData> data;

    public final ArrayList<SigTypeNewsData> getData() {
        return data;
    }

    public final void setData(ArrayList<SigTypeNewsData> data) {
        this.data = data;
    }
}