package com.liuwan.mydesign.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by liuwan on 2016/11/11.
 * ViewPager 滚动速度设置
 */
public class ViewPagerScroller extends Scroller {

    private int mScrollDuration = 2000; // 默认滑动速度

    /**
     * 设置速度速度
     */
    public void setScrollDuration(int duration) {
        this.mScrollDuration = duration;
    }

    public ViewPagerScroller(Context context) {
        super(context);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    public static void setViewPagerScrollDuration(ViewPager viewPager, int speed) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            ViewPagerScroller viewPagerScroller = new ViewPagerScroller(viewPager.getContext(),
                    new OvershootInterpolator(0.6F));
            mScroller.set(viewPager, viewPagerScroller);
            viewPagerScroller.setScrollDuration(speed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
