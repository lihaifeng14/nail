package com.nail.news.data;

import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class CommentsDetailData implements IBaseContent {
    private String comment_id; // 299757490,
    private String comment_date; // "2014-10-18  23:07",
    private String uname; // "客户端用户",
    private String comment_contents; // "这货不是人养的    狗渣碎",
    private String ip_from; // "中国",
    private int uptimes; // "0",
    private boolean expand; // 内部字段，是否展开
    private ArrayList<CommentsParentData> parent;

    public final boolean isExpand() {
        return expand;
    }

    public final void setExpand(boolean expand) {
        this.expand = expand;
    }

    public final String getComment_id() {
        return comment_id;
    }

    public final void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public final String getComment_date() {
        return comment_date;
    }

    public final void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    public final String getUname() {
        return uname;
    }

    public final void setUname(String uname) {
        this.uname = uname;
    }

    public final String getComment_contents() {
        return comment_contents;
    }

    public final void setComment_contents(String comment_contents) {
        this.comment_contents = comment_contents;
    }

    public final String getIp_from() {
        return ip_from;
    }

    public final void setIp_from(String ip_from) {
        this.ip_from = ip_from;
    }

    public final int getUptimes() {
        return uptimes;
    }

    public final void setUptimes(int uptimes) {
        this.uptimes = uptimes;
    }

    public final ArrayList<CommentsParentData> getParent() {
        return parent;
    }

    public final void setParent(ArrayList<CommentsParentData> parent) {
        this.parent = parent;
    }

    public static class CommentsParentData implements IBaseContent {
        private String uname; // "客户端用户",
        private String comment_contents; // "支持",
        private String ip_from; // "广东省深圳市",
        private String ext2; // ""
        public final String getUname() {
            return uname;
        }
        public final void setUname(String uname) {
            this.uname = uname;
        }
        public final String getComment_contents() {
            return comment_contents;
        }
        public final void setComment_contents(String comment_contents) {
            this.comment_contents = comment_contents;
        }
        public final String getIp_from() {
            return ip_from;
        }
        public final void setIp_from(String ip_from) {
            this.ip_from = ip_from;
        }
        public final String getExt2() {
            return ext2;
        }
        public final void setExt2(String ext2) {
            this.ext2 = ext2;
        }
    }
}