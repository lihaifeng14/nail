package com.nail.news.widget;

import com.nail.news.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PictureIndicator extends LinearLayout {

    private Context mContext;

    public PictureIndicator(Context context) {
        super(context);
        init(context);
    }

    public PictureIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(HORIZONTAL);
    }

    public void setSize(int size) {
        removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(mContext);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.picture_indicator_inteval);
            imageView.setImageResource(R.drawable.select_indicator_picture);
            imageView.setPadding(padding, 0, 0, 0);
            imageView.setEnabled(false);
            addView(imageView);
        }
    }

    public void setCheck(int position) {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View view = getChildAt(i);
            if (i == position) {
                view.setEnabled(true);
            } else {
                view.setEnabled(false);
            }
        }
    }
}