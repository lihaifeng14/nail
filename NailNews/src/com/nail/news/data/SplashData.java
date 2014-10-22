package com.nail.news.data;

import com.nail.core.http.IBaseContent;

public class SplashData implements IBaseContent {
    private SplashInfo cover;

    public final SplashInfo getCover() {
        return cover;
    }
    public final void setCover(SplashInfo cover) {
        this.cover = cover;
    }

    public static class SplashInfo implements IBaseContent {
        private int duration; // "3",
        private long updateTime; // "1413813920991",
        private String coverImage; // "http://y3.ifengimg.com/a/2014_43/a1be507d0e7b59b.jpg"

        public final int getDuration() {
            return duration;
        }
        public final void setDuration(int duration) {
            this.duration = duration;
        }
        public final long getUpdateTime() {
            return updateTime;
        }
        public final void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
        public final String getCoverImage() {
            return coverImage;
        }
        public final void setCoverImage(String coverImage) {
            this.coverImage = coverImage;
        }
    }
}