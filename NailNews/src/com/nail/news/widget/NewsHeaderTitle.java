package com.nail.news.widget;

import com.nail.news.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class NewsHeaderTitle extends RelativeLayout {

    private Context mContext;

    public NewsHeaderTitle(Context context) {
        super(context);
        init(context);
    }

    public NewsHeaderTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NewsHeaderTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setBackgroundResource(R.color.title_background);
    }
}