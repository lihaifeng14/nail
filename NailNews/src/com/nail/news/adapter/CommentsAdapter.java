package com.nail.news.adapter;

import com.nail.news.R;
import com.nail.news.data.CommentsContent;
import com.nail.news.data.CommentsDetailData;
import com.nail.news.data.CommentsDetailData.CommentsParentData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter implements
        View.OnClickListener {

    private CommentsContent mContent;
    private Context mContext;

    public CommentsAdapter(Context context) {
        mContext = context;
    }

    public void setContent(CommentsContent content) {
        mContent = content;
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

        if (mContent.isHeader(position)) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_comments_title, null);
                ViewHolder1 holder = new ViewHolder1();
                holder.text_title = (TextView) convertView
                        .findViewById(R.id.text_comments);
                convertView.setTag(holder);
            }

            ViewHolder1 holder = (ViewHolder1) convertView.getTag();
            if (position == 0 && mContent.mHottestData != null
                    && mContent.mHottestData.size() > 0) {
                holder.text_title.setText(R.string.hotest_comments);
            } else {
                holder.text_title.setText(R.string.newest_comments);
            }
        } else {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_comments, null);
                ViewHolder holder = new ViewHolder();
                holder.comments_floor = (FrameLayout) convertView
                        .findViewById(R.id.comments_floor);
                holder.comments_from = (TextView) convertView
                        .findViewById(R.id.comments_from);
                holder.comments_msg = (TextView) convertView
                        .findViewById(R.id.comments_msg);
                holder.comments_user = (TextView) convertView
                        .findViewById(R.id.comments_user);
                holder.comments_support = (TextView) convertView
                        .findViewById(R.id.comments_support);
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            CommentsDetailData data = null;
            if (mContent.mHottestData != null
                    && mContent.mHottestData.size() > 0) {
                if (position <= mContent.mHottestData.size()) {
                    data = mContent.mHottestData.get(position - 1);
                } else {
                    data = mContent.mNewestData.get(position - 2
                            - mContent.mHottestData.size());
                }
            } else {
                data = mContent.mNewestData.get(position - 1);
            }
            holder.comments_user.setText(data.getUname());
            holder.comments_from.setText(data.getIp_from());
            holder.comments_msg.setText(data.getComment_contents());
            if (data.getUptimes() > 0) {
                holder.comments_support.setText(String.valueOf(data
                        .getUptimes()));
            } else {
                holder.comments_support.setText("");
            }

            buildCommentsFloor(holder.comments_floor, data);
        }
        return convertView;
    }

    private static final int FLOOR_HIDE_COUNT = 7;
    private static final int VISIBLE_FLOOR_COUNT = 3;

    private void buildCommentsFloor(FrameLayout layout, CommentsDetailData data) {
        if (data.getParent() == null || data.getParent().size() == 0) {
            layout.setVisibility(View.GONE);
            return;
        }

        int size = data.getParent().size();
        layout.setVisibility(View.VISIBLE);
        layout.removeAllViews();

        ViewGroup parent = layout;
        if (size > FLOOR_HIDE_COUNT && !data.isExpand()) {
            for (int i = 0; i < 3; i++) {
                parent = addCommentFloorData(parent, data.getParent().get(i), size - i);
            }
            parent = addCommentExpandData(parent, data);
            for (int i = size-3; i < size; i++) {
                parent = addCommentFloorData(parent, data.getParent().get(i), size - i);
            }
        } else {
            for (int i = 0; i < size; i++) {
                parent = addCommentFloorData(parent, data.getParent().get(i), size - i);
            }
        }
    }

    private ViewGroup addCommentFloorData(ViewGroup parent,
            CommentsParentData data, int floor) {

        ViewGroup floorView = (ViewGroup) LayoutInflater.from(mContext)
                .inflate(R.layout.comments_floor, null);
        View commentView = floorView.findViewById(R.id.floor_comments);
        View expandView = floorView.findViewById(R.id.floor_expand);
        TextView textUser = (TextView) floorView.findViewById(R.id.floor_user);
        TextView textFloor = (TextView) floorView.findViewById(R.id.floor_num);
        TextView textMsg = (TextView) floorView.findViewById(R.id.floor_msg);
        commentView.setVisibility(View.VISIBLE);
        expandView.setVisibility(View.GONE);
        textUser.setText(data.getUname() + "[" + data.getIp_from() + "]");
        textFloor.setText(String.valueOf(floor));
        textMsg.setText(data.getComment_contents());
        parent.addView(floorView, 0);
        return floorView;
    }

    private ViewGroup addCommentExpandData(ViewGroup parent,
            CommentsDetailData data) {

        ViewGroup floorView = (ViewGroup) LayoutInflater.from(mContext)
                .inflate(R.layout.comments_floor, null);
        View commentView = floorView.findViewById(R.id.floor_comments);
        View expandView = floorView.findViewById(R.id.floor_expand);
        commentView.setVisibility(View.GONE);
        expandView.setVisibility(View.VISIBLE);
        expandView.setTag(data);
        expandView.setOnClickListener(this);
        parent.addView(floorView, 0);
        return floorView;
    }

    public static class ViewHolder1 {
        public TextView text_title;
    }

    public static class ViewHolder {
        public FrameLayout comments_floor;
        public TextView comments_msg;
        public TextView comments_user;
        public TextView comments_from;
        public TextView comments_support;
    }

    @Override
    public int getItemViewType(int position) {
        if (mContent != null && mContent.isHeader(position)) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.floor_expand:
            CommentsDetailData data = (CommentsDetailData) v.getTag();
            data.setExpand(true);
            notifyDataSetChanged();
            break;
        }
    }

}