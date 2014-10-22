package com.nail.news.activity;

import java.util.List;

import com.nail.core.imageloader.ImageLoader;
import com.nail.news.R;
import com.nail.news.data.NewsDetailData;
import com.nail.news.data.NewsItemData.SlideData;
import com.nail.news.fragment.BaseFragment;
import com.nail.news.fragment.PicNewsFragment;
import com.nail.news.manager.NewsDetailManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;

public class PicturesActivity extends FragmentActivity implements
        NewsDetailManager.NewDetailCallback, View.OnClickListener, BaseFragment.NotifyFragment,
        ViewPager.OnPageChangeListener {

    public static final String EXTRA_DOCUMENT_ID = "extra_document_id";
    public static final String EXTRA_COMMENTS_COUNT = "extra_comments_count";

    private NewsDetailManager mManager;

    private String mDocumentId;
    private String mCommentUrl;

    private View mBackView;
    private View mCommentsView;
    private TextView mComments;

    private TextView mTextTitle;
    private TextView mTextNumber;
    private TextView mTextContent;

    private ViewPager mPager;
    private List<SlideData> mSlideData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        mManager = NewsDetailManager.getInstance();

        mBackView = findViewById(R.id.button_back);
        mBackView.setOnClickListener(this);
        mCommentsView = findViewById(R.id.button_comments);
        mComments = (TextView)findViewById(R.id.text_comments);
        mComments.setText(getIntent().getIntExtra(EXTRA_COMMENTS_COUNT, 0)+"评论");
        mCommentsView.setOnClickListener(this);

        mTextContent = (TextView)findViewById(R.id.picture_content);
        mTextNumber = (TextView)findViewById(R.id.picture_num);
        mTextTitle = (TextView)findViewById(R.id.picture_title);
        mTextContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        mPager = (ViewPager)findViewById(R.id.pictures_pager); 

        mDocumentId = getIntent().getStringExtra(EXTRA_DOCUMENT_ID);
        mManager.getDetailData(mDocumentId, this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
        case R.id.button_back:
            finish();
            break;
        case R.id.button_comments:
            if (mCommentUrl == null) {
                return;
            }
            Intent intent = new Intent(this, CommentsActivity.class);
            intent.putExtra(CommentsActivity.EXTRA_COMMENTS_URL, mCommentUrl);
            startActivity(intent);
        }
    }

    @Override
    public void onDetailCallback(NewsDetailData data) {
        mSlideData = data.getData().getBody().getSlides();
        mCommentUrl = data.getData().getBody().getCommentsUrl();
        FragmentPagerAdapter adapter = new PicturesPageAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(this);
        updatePictureTitle(0);
    }

    @Override
    public void onDetailFailed() {
    }

    @Override
    public void onFragmentAttached(BaseFragment fragment) {
    }

    @Override
    public void onFragmentCreatedView(BaseFragment fragment) {
    }

    public class PicturesPageAdapter extends FragmentPagerAdapter {

        public PicturesPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return new PicturesFragment();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PicturesFragment fragment = (PicturesFragment)(super.instantiateItem(container, position));
            if (mSlideData != null) {
                fragment.setUrl(mSlideData.get(position).getImage());
            }
            return fragment;
        }

        @Override
        public int getCount() {
            if (mSlideData != null) {
                return mSlideData.size();
            }
            return 0;
        }
        
    }

    public static class PicturesFragment extends BaseFragment {
        private String mPictureUrl;
        private ImageLoader mImageLoader;

        public void setUrl(String url) {
            mPictureUrl = url;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            mImageLoader = ImageLoader.getInstance(mActivity.getApplicationContext());
            View view = inflater.inflate(R.layout.item_picture_pager, null);
            ImageView imageView = (ImageView)view.findViewById(R.id.picture_pager);
            mImageLoader.displayImage(mPictureUrl, imageView, R.drawable.item_picture_background_default, null);
            return view;
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
        updatePictureTitle(arg0);
    }

    private void updatePictureTitle(int position) {
        if (mSlideData == null || mSlideData.size() <= position) {
            return;
        }
        SlideData data = mSlideData.get(position);
        mTextContent.setText(data.getDescription());
        mTextTitle.setText(data.getTitle());
        mTextNumber.setText(String.valueOf(position+1)+"/"+mSlideData.size());
    }
}