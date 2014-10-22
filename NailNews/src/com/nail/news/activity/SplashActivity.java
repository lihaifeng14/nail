package com.nail.news.activity;

import com.nail.core.imageloader.ImageLoader;
import com.nail.news.R;
import com.nail.news.data.SplashData.SplashInfo;
import com.nail.news.manager.SplashManager;
import com.nail.news.manager.SplashManager.SplashCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity implements SplashCallback{

    private ImageLoader mImageLoader;
    private SplashManager mManager;
    private Handler mHandler;
    private ImageView mImageSplash;
    private Animation mAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mImageLoader = ImageLoader.getInstance(this);
        mManager = SplashManager.getInstance();
        mHandler = new Handler();

        mImageSplash = (ImageView)findViewById(R.id.splash_img);
        mManager.getSplashInfo(this);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, NewsMainActivity.class));
                finish();
            }
        }, 4000);

        mAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
    }

    @Override
    public void onSplashCallback(SplashInfo info) {
        mImageLoader.displayImage(info.getCoverImage(), mImageSplash, 0, null);
        mImageSplash.startAnimation(mAnimation);
    }

    @Override
    public void onSplashFailed() {
    }
    
}