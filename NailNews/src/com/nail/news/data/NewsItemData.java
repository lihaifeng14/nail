package com.nail.news.data;

import java.util.ArrayList;

import com.nail.core.http.IBaseContent;

public class NewsItemData implements IBaseContent {

    private String cate; // http://i.ifeng.com/jpadnews?id=i.ifeng.com/aid=003&imgwidth=480&type=list&pagesize=20",
    private String text; // 
    private String wwwurl; // "http://wap.ifeng.com/news.jsp?aid=90638971",
    private String thumbnail; // "http://d.ifengimg.com/w150_h106/a0.ifengimg.com/autoimg/19/01/2000119_8.jpg"
    private String title; // "2014英国最佳跨界SUV排行榜 逍客夺冠"
    private String source; // "凤凰汽车"
    private String channel; // "新闻"
    private String editTime; // "2014/10/15 00:45:00"
    private String updateTime; // "2014/10/15 00:45:00"
    private String id; // "http://api.3g.ifeng.com/ipadtestdoc?aid=90500161&channel=%E6%96%B0%E9%97%BB"
    private String documentId; // "imcp_90500161"
    private String type; // "list"
    private String introduction; // ""
    private String hasVideo; // "N"
    private String hasSurvey; // "N"
    private String commentsUrl; // "http://auto.ifeng.com/haiwai/20141015/1026223.shtml"
    private int comments; // 65
    private int commentsAll; // 149
    private ArrayList<Extension> extensions;
    private ArrayList<LinkData> links;
    private ArrayList<ImageData> img;
    private ArrayList<SlideData> slides;

    public final ArrayList<SlideData> getSlides() {
        return slides;
    }
    public final void setSlides(ArrayList<SlideData> slides) {
        this.slides = slides;
    }
    public final ArrayList<ImageData> getImg() {
        return img;
    }
    public final void setImg(ArrayList<ImageData> img) {
        this.img = img;
    }
    public final ArrayList<Extension> getExtensions() {
        return extensions;
    }
    public final void setExtensions(ArrayList<Extension> extensions) {
        this.extensions = extensions;
    }
    public final ArrayList<LinkData> getLinks() {
        return links;
    }
    public final void setLinks(ArrayList<LinkData> links) {
        this.links = links;
    }
    public final String getThumbnail() {
        return thumbnail;
    }
    public final void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public final String getTitle() {
        return title;
    }
    public final void setTitle(String title) {
        this.title = title;
    }
    public final String getSource() {
        return source;
    }
    public final void setSource(String source) {
        this.source = source;
    }
    public final String getChannel() {
        return channel;
    }
    public final void setChannel(String channel) {
        this.channel = channel;
    }
    public final String getEditTime() {
        return editTime;
    }
    public final void setEditTime(String editTime) {
        this.editTime = editTime;
    }
    public final String getUpdateTime() {
        return updateTime;
    }
    public final void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public final String getId() {
        return id;
    }
    public final void setId(String id) {
        this.id = id;
    }
    public final String getDocumentId() {
        return documentId;
    }
    public final void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    public final String getType() {
        return type;
    }
    public final void setType(String type) {
        this.type = type;
    }
    public final String getIntroduction() {
        return introduction;
    }
    public final void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
    public final String getHasVideo() {
        return hasVideo;
    }
    public final void setHasVideo(String hasVideo) {
        this.hasVideo = hasVideo;
    }
    public final String getHasSurvey() {
        return hasSurvey;
    }
    public final void setHasSurvey(String hasSurvey) {
        this.hasSurvey = hasSurvey;
    }
    public final String getCommentsUrl() {
        return commentsUrl;
    }
    public final void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }
    public final int getComments() {
        return comments;
    }
    public final void setComments(int comments) {
        this.comments = comments;
    }
    public final int getCommentsAll() {
        return commentsAll;
    }
    public final void setCommentsAll(int commentsAll) {
        this.commentsAll = commentsAll;
    }

    public static class Extension implements IBaseContent {
        private String type;
        private String style;

        public final String getType() {
            return type;
        }
        public final void setType(String type) {
            this.type = type;
        }
        public final String getStyle() {
            return style;
        }
        public final void setStyle(String style) {
            this.style = style;
        }
    }

    public static class LinkData implements IBaseContent {
        private String type; // "slide",
        private String url; // "http://api.3g.ifeng.com/ipadtestdoc?aid=90530876"
        public final String getType() {
            return type;
        }
        public final void setType(String type) {
            this.type = type;
        }
        public final String getUrl() {
            return url;
        }
        public final void setUrl(String url) {
            this.url = url;
        }
    }

    public final String getCate() {
        return cate;
    }
    public final void setCate(String cate) {
        this.cate = cate;
    }
    public final String getText() {
        return text;
    }
    public final void setText(String text) {
        this.text = text;
    }
    public final String getWwwurl() {
        return wwwurl;
    }
    public final void setWwwurl(String wwwurl) {
        this.wwwurl = wwwurl;
    }

    public static class ImageData implements IBaseContent {
        private String url;
        private SizeData size;

        public final SizeData getSize() {
            return size;
        }

        public final void setSize(SizeData size) {
            this.size = size;
        }

        public final String getUrl() {
            return url;
        }

        public final void setUrl(String url) {
            this.url = url;
        }
    }

    public static class SizeData implements IBaseContent {
        private int width;
        private int height;
        public final int getWidth() {
            return width;
        }
        public final void setWidth(int width) {
            this.width = width;
        }
        public final int getHeight() {
            return height;
        }
        public final void setHeight(int height) {
            this.height = height;
        }
    }

    public static class SlideData implements IBaseContent {
        private String image;
        private String title;
        private String description;
        public final String getImage() {
            return image;
        }
        public final void setImage(String image) {
            this.image = image;
        }
        public final String getTitle() {
            return title;
        }
        public final void setTitle(String title) {
            this.title = title;
        }
        public final String getDescription() {
            return description;
        }
        public final void setDescription(String description) {
            this.description = description;
        }
    }
}