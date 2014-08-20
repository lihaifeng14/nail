package com.nail.nailtest;

import com.nail.core.imageloader.ImageLoader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageLoadAcitvity extends Activity {
    public String[] mImageUrls = {
            "http://imgstatic.baidu.com/img/image/shouye/fanbingbing.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/liuyifei.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/wanglihong.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/gaoyuanyuan.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/yaodi.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/zhonghanliang.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/xiezhen.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/yiping3.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/erping4.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/hangeng.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/liuyan1.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/liushishi1.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/sunli1.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/tangyan1.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/zhanggenshuo1.jpg",
            "http://imgstatic.baidu.com/img/image/shouye/xiaohua0605.jpg"};

    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageload);

        mImageLoader = ImageLoader.getInstance(this);
        GridView gridView = (GridView)findViewById(R.id.image_list);
        ImageAdapter adapter = new ImageAdapter();
        gridView.setAdapter(adapter);
    }

    public class ImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mImageUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return mImageUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ImageLoadAcitvity.this).inflate(R.layout.grid_item, null);
            }
            ImageView imageView = (ImageView)convertView.findViewById(R.id.image);
            Log.d("lihaifeng", "displayImage position " + position + " ImageView " + imageView + " Url " + mImageUrls[position]);
            mImageLoader.displayImage(mImageUrls[position], imageView, R.drawable.ic_launcher, null);
            return convertView;
        }
        
    }
}