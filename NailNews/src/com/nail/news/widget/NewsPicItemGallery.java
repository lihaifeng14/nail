package com.nail.news.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Gallery;

public class NewsPicItemGallery extends Gallery{

    private int mStartX, mStartY;

    public NewsPicItemGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mStartX = (int)ev.getX();
            mStartY = (int)ev.getY();
            break;

        case MotionEvent.ACTION_MOVE:
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            if (Math.abs(mStartY-y) * 2 >= Math.abs(mStartY-x)) {
                return false;
            } else {
                return true;
            }

        case MotionEvent.ACTION_CANCEL:
            break;
        case MotionEvent.ACTION_UP:
            break;

        default:
            break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mStartX = (int) event.getX();
            mStartY = (int) event.getY();
            break;
        case MotionEvent.ACTION_MOVE:
            int x = (int) event.getX();
            int y = (int) event.getY();
            if(Math.abs(mStartY-y) >Math.abs(mStartY-x)*2) {
                return false;
            }
        default:
            break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        int event;
        if (e2.getX() > e1.getX()) {
            onScroll(null, null, -1.0f, 0.0f);
            event = KeyEvent.KEYCODE_DPAD_LEFT;
        } else {
            onScroll(null, null, 1.0f, 0.0f);
            event = KeyEvent.KEYCODE_DPAD_RIGHT;
        }
        onKeyDown(event, null);
        return true;
    }
}