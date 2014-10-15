package com.nail.core.widget.pageindicator;

import android.support.v4.view.ViewPager;

public interface IPageIndicator extends ViewPager.OnPageChangeListener {

    public interface TabSelectListener {
        public void onTabSelect(int position);
    }

    void setTabSelectListener(TabSelectListener listener);

    void setViewPager(ViewPager view);

    void setViewPager(ViewPager view, int initialPosition);

    void setCurrentItem(int item);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyDataSetChanged();
}