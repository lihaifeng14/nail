package com.nail.core.widget.pageindicator;

import com.nail.core.R;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TabPageIndicator extends HorizontalScrollView implements IPageIndicator, View.OnClickListener {

    private LinearLayout mLinearLayout;
    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mPageListener;
    private TabSelectListener mTabListener;

    private int mTabIndex;

    private Context mContext;
    private Handler mHandler;

    public TabPageIndicator(Context context) {
        super(context);
   }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabPageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mHandler = new Handler(context.getMainLooper());

        setHorizontalScrollBarEnabled(false);

        // 添加一个LinearLayout到ScrollView中
        mLinearLayout = new LinearLayout(context);
        mLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        addView(mLinearLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        mTabIndex = -1;
    }

    @Override
    public void setTabSelectListener(TabSelectListener listener) {
        mTabListener = listener;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mPageListener != null) {
            mPageListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mPageListener != null) {
            mPageListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mPageListener != null) {
            mPageListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }

        mViewPager = view;
        mViewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            return;
        }
        if (mTabIndex == item) {
            return;
        }

        if (mTabIndex != -1) {
            TextView text = (TextView)mLinearLayout.getChildAt(mTabIndex);
            text.setSelected(false);
        }
        TextView text = (TextView)mLinearLayout.getChildAt(item);
        text.setSelected(true);
        mTabIndex = item;
        mViewPager.setCurrentItem(item);
        animateToTab(mTabIndex);
    }

    private void animateToTab(final int position) {
        final View tabView = mLinearLayout.getChildAt(position);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
            }
        });
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mPageListener = listener;
    }

    @Override
    public void notifyDataSetChanged() {
        mLinearLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            addTab(i, title);
        }
        requestLayout();
    }

    private void addTab(int index, CharSequence text) {
        TextView textView = new TextView(mContext);
        textView.setTag(String.valueOf(index));
        textView.setFocusable(true);
        textView.setOnClickListener(this);
        textView.setText(text);
        textView.setPadding(15, 10, 15, 10);
        textView.setTextAppearance(mContext, R.style.tabindicator_text_style);
        textView.setTextColor(getResources().getColorStateList(R.color.tabindicator_text_color));
        textView.setBackgroundResource(R.drawable.tabindicator_background_selector);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        param.leftMargin = 10;
        param.rightMargin = 10;
        mLinearLayout.addView(textView, param);
    }

    @Override
    public void onClick(View v) {
        int index = Integer.valueOf((String)(v.getTag()));
        setCurrentItem(index);
        if (mTabListener != null) {
            mTabListener.onTabSelect(index);
        }
    }
}