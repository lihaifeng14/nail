package com.nail.news.activity;


import com.nail.core.widget.pageindicator.TabPageIndicator;
import com.nail.news.R;
import com.nail.news.data.PageContent;
import com.nail.news.fragment.BaseFragment;
import com.nail.news.fragment.PicNewsFragment;
import com.nail.news.manager.PageManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class NewsMainActivity extends FragmentActivity implements 
        BaseFragment.NotifyFragment, ViewPager.OnPageChangeListener{

    private PicNewsFragment[] mListFragments;
    private PageManager mManager;
    private int mNeedLoadPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsmain);

        mManager = PageManager.getInstance();
        mListFragments = new PicNewsFragment[mManager.getTypes().size()];

        FragmentPagerAdapter adapter = new NailNewsPageAdapter(getSupportFragmentManager());
 
        ViewPager pager = (ViewPager)findViewById(R.id.news_main_pager); 
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.news_main_tabindicator);
        indicator.setOnPageChangeListener(this);        indicator.setViewPager(pager, 0);
        mNeedLoadPosition = 0;
     }

    class NailNewsPageAdapter extends FragmentPagerAdapter {

        public NailNewsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // 此处用来记录所有使用的fragment，不论是new还是缓存的
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PicNewsFragment fragment = (PicNewsFragment)(super.instantiateItem(container, position));
            fragment.setType(mManager.getTypes().get(position));
            mListFragments[position] = fragment;
            return fragment;
        }

        @Override
        public Fragment getItem(int position) {
            PicNewsFragment fragment = new PicNewsFragment();
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            PageContent content = PageManager.getInstance().getPageByIndex(position);
            if (content != null) {
                return content.mTitle;
            }
            return null;
        }

        @Override
        public int getCount() {
          return mManager.getTypes().size();
        }

        @Override
        public void notifyDataSetChanged() {
            mListFragments = new PicNewsFragment[mManager.getTypes().size()];
            super.notifyDataSetChanged();
        }
    }

    @Override
    public void onFragmentAttached(BaseFragment fragment) {
    }

    @Override
    public void onFragmentCreatedView(BaseFragment fragment) {
        if (mNeedLoadPosition != -1) {
            if (mListFragments[mNeedLoadPosition] == fragment) {
                mNeedLoadPosition = -1;
                PicNewsFragment picFragment = (PicNewsFragment)fragment;
                picFragment.onFragmentShow();
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        if (mListFragments[arg0] != null) {
            mListFragments[arg0].onFragmentShow();
        }
    }
}
