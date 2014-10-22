package com.nail.news.data;

import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class CommentsData implements IBaseContent {
    private CommentsAllData data;

    public final CommentsAllData getData() {
        return data;
    }

    public final void setData(CommentsAllData data) {
        this.data = data;
    }

    public static class CommentsAllData implements IBaseContent {
        private int count; // 2998,
        private int join_count; // 17239,
        private CommentsTypeData comments;

        public final int getCount() {
            return count;
        }

        public final void setCount(int count) {
            this.count = count;
        }

        public final int getJoin_count() {
            return join_count;
        }

        public final void setJoin_count(int join_count) {
            this.join_count = join_count;
        }

        public final CommentsTypeData getComments() {
            return comments;
        }

        public final void setComments(CommentsTypeData comments) {
            this.comments = comments;
        }
    }

    public static class CommentsTypeData implements IBaseContent {
        private ArrayList<CommentsDetailData>  hottest;
        private ArrayList<CommentsDetailData>  newest;

        public final ArrayList<CommentsDetailData> getHottest() {
            return hottest;
        }
        public final void setHottest(ArrayList<CommentsDetailData> hottest) {
            this.hottest = hottest;
        }
        public final ArrayList<CommentsDetailData> getNewest() {
            return newest;
        }
        public final void setNewest(ArrayList<CommentsDetailData> newest) {
            this.newest = newest;
        }
    }

}