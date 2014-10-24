package com.nail.news.adapter;

import com.nail.core.imageloader.ImageLoader;
import com.nail.news.R;
import com.nail.news.activity.NewsDetailActivity;
import com.nail.news.activity.PicturesActivity;
import com.nail.news.data.NewsItemData;
import com.nail.news.data.NewsItemData.Extension;
import com.nail.news.data.PageContent;
import com.nail.news.widget.NewsPicItemGallery;
import com.nail.news.widget.PictureIndicator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.TextView;

public class NewsFragmentAdapter extends BaseAdapter implements
        View.OnClickListener {

    private PageContent mContent;
    private Context mContext;
    private ViewGroup mParentView;

    public NewsFragmentAdapter(Context context) {
        mContext = context;
    }

    public void setContent(PageContent content) {
        mContent = content;
    }

    public void setParentView(ViewGroup parent) {
        mParentView = parent;
    }

    @Override
    public int getCount() {
        if (mContent != null) {
            return mContent.getItemCount();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mContent == null) {
            return null;
        }

        if (position == 0 && mContent.hasFocusData()) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_pictures, null);
                ViewHolder1 holder = new ViewHolder1();
                holder.pic_gallery = (NewsPicItemGallery) convertView
                        .findViewById(R.id.item_picture_gallery);
                holder.text_title = (TextView) convertView
                        .findViewById(R.id.item_picture_title);
                holder.text_num = (PictureIndicator) convertView
                        .findViewById(R.id.item_picture_num);
                convertView.setTag(holder);
            }

            ViewHolder1 holder = (ViewHolder1) convertView.getTag();
            NewsPictureGalleryAdapter adapter = new NewsPictureGalleryAdapter();
            adapter.mTextTitle = holder.text_title;
            adapter.mTextNum = holder.text_num;
            if (adapter.getCount() <= 1) {
                holder.text_num.setVisibility(View.GONE);
                holder.text_num.setSize(0);
            } else {
                holder.text_num.setVisibility(View.VISIBLE);
                holder.text_num.setSize(adapter.getCount());
            }
            holder.pic_gallery.setAdapter(adapter);
            holder.pic_gallery.setParentView(mParentView);
            holder.pic_gallery.setSelection(0);
            holder.pic_gallery.setOnItemClickListener(adapter);
            holder.pic_gallery.setOnItemSelectedListener(adapter);
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_news, null);
                ViewHolder holder = new ViewHolder();
                holder.item_title = (TextView) convertView
                        .findViewById(R.id.item_title);
                holder.item_title2 = (TextView) convertView
                        .findViewById(R.id.item_title2);
                holder.item_source = (TextView) convertView
                        .findViewById(R.id.item_source);
                holder.comment_count = (TextView) convertView
                        .findViewById(R.id.comment_count);
                holder.publish_time = (TextView) convertView
                        .findViewById(R.id.publish_time);
                holder.right_image = (ImageView) convertView
                        .findViewById(R.id.right_image);
                holder.item_layout = convertView.findViewById(R.id.item_layout);
                holder.title_layout = convertView
                        .findViewById(R.id.title_layout);
                holder.image_layout = convertView
                        .findViewById(R.id.image_layout);
                holder.below_image1 = (ImageView) convertView
                        .findViewById(R.id.below_image1);
                holder.below_image2 = (ImageView) convertView
                        .findViewById(R.id.below_image2);
                holder.below_image3 = (ImageView) convertView
                        .findViewById(R.id.below_image3);
                convertView.setTag(holder);
            }

            int pos = mContent.hasFocusData() ? position - 1 : position;
            NewsItemData data = mContent.mNewsData.get(pos);
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.item_layout.setOnClickListener(this);
            holder.comment_count.setText(data.getComments() + "评论");
            holder.publish_time.setText(data.getUpdateTime());
            holder.item_source.setText(data.getSource());
            holder.position = pos;
            holder.title_layout.setVisibility(View.GONE);
            holder.image_layout.setVisibility(View.GONE);
            if (data.getHasSlide() != null
                    && data.getHasSlide().compareTo("Y") == 0
                    && data.getExtensions() != null) {
                holder.image_layout.setVisibility(View.VISIBLE);
                holder.item_title2.setText(data.getTitle());
                Extension extention = data.getExtensions().get(0);
                ImageLoader.getInstance(mContext).displayImage(
                        extention.getImages().get(0), holder.below_image1,
                        R.drawable.item_background_default, null);
                ImageLoader.getInstance(mContext).displayImage(
                        extention.getImages().get(1), holder.below_image2,
                        R.drawable.item_background_default, null);
                ImageLoader.getInstance(mContext).displayImage(
                        extention.getImages().get(2), holder.below_image3,
                        R.drawable.item_background_default, null);
            } else {
                holder.title_layout.setVisibility(View.VISIBLE);
                holder.item_title.setText(data.getTitle());
                ImageLoader.getInstance(mContext).displayImage(
                        data.getThumbnail(), holder.right_image,
                        R.drawable.item_background_default, null);
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mContent != null && mContent.hasFocusData()) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    static class ViewHolder {

        View item_layout;
        View title_layout;
        View image_layout;
        TextView item_title;
        TextView item_title2;
        TextView item_source;
        TextView comment_count;
        TextView publish_time;
        ImageView right_image;
        ImageView below_image1;
        ImageView below_image2;
        ImageView below_image3;
        int position;
    }

    static class ViewHolder1 {

        NewsPicItemGallery pic_gallery;
        TextView text_title;
        PictureIndicator text_num;
    }

    @Override
    public void onClick(View v) {
        if (mContent == null) {
            return;
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        NewsItemData data = mContent.mNewsData.get(holder.position);

        Intent intent = null;
        if (data.getHasSlide() != null
                && data.getHasSlide().compareTo("Y") == 0
                && data.getExtensions() != null) {
            intent = new Intent(mContext, PicturesActivity.class);
        } else {
            intent = new Intent(mContext, NewsDetailActivity.class);
        }
        intent.putExtra(NewsDetailActivity.EXTRA_DOCUMENT_ID,
                data.getDocumentId());
        intent.putExtra(NewsDetailActivity.EXTRA_COMMENTS_COUNT,
                data.getComments());
        mContext.startActivity(intent);
    }

    public class NewsPictureGalleryAdapter extends BaseAdapter implements
            AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

        public TextView mTextTitle;
        public PictureIndicator mTextNum;

        @Override
        public int getCount() {
            if (mContent == null || mContent.mFocusData == null) {
                return 0;
            }
            return mContent.mFocusData.getBody().getItem().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_picture_gallery, null);
            }
            ImageView img = (ImageView) convertView
                    .findViewById(R.id.picture_gallery);
            if (mContent != null && mContent.mFocusData != null) {
                NewsItemData data = mContent.mFocusData.getBody().getItem()
                        .get(position);
                ImageLoader.getInstance(mContext).displayImage(
                        data.getThumbnail(), img,
                        R.drawable.item_picture_background_default, null);
            }
            return convertView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            if (mContent != null && mContent.mFocusData != null) {
                int count = mContent.mFocusData.getBody().getItem().size();
                NewsItemData data = mContent.mFocusData.getBody().getItem()
                        .get(position);
                mTextTitle.setText(data.getTitle());
                mTextNum.setCheck(position);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            if (mContent != null && mContent.mFocusData != null) {
                int count = mContent.mFocusData.getBody().getItem().size();
                NewsItemData data = mContent.mFocusData.getBody().getItem()
                        .get(position);
                Intent intent = new Intent(mContext, PicturesActivity.class);
                intent.putExtra(NewsDetailActivity.EXTRA_DOCUMENT_ID,
                        data.getDocumentId());
                intent.putExtra(NewsDetailActivity.EXTRA_COMMENTS_COUNT,
                        data.getComments());
                mContext.startActivity(intent);
            }
        }
    }
}