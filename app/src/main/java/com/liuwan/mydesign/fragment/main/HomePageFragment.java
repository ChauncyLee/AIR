package com.liuwan.mydesign.fragment.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.activity.DtuListActivity;
import com.liuwan.mydesign.activity.MapWarningActivity;
import com.liuwan.mydesign.activity.ParameterSelectionActivity;
import com.liuwan.mydesign.adapter.GridViewAdapter;
import com.liuwan.mydesign.widget.ViewPagerScroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuwan on 2016/9/24.
 * 首页界面
 */
public class HomePageFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private ViewGroup viewGroup;
    private ViewPager viewPager;
    private ImageView[] indicator;
    private ImageView[] picture;
    private ImageView imageView;

    private LinearLayout gridViewParent;
    private GridView gridView;

    /**
     * 计时线程
     */
    private Timer timer;
    private TimerTask task;

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = 1;
            handler.sendMessage(msg);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                int num = viewPager.getCurrentItem() == 3 ? 0 : viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(num, true);
            }
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewGroup = (ViewGroup) getActivity().findViewById(R.id.view_Group);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewPager);
        gridViewParent = (LinearLayout) getActivity().findViewById(R.id.gridViewParent);
        gridView = (GridView) getActivity().findViewById(R.id.gridView);

        initViewPager();
        initGridView();

        timer = new Timer();
        task = new MyTimerTask();
        timer.schedule(task, 4000, 4000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_homepage, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * ViewPager.OnPageChangeListener
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0 % picture.length);
    }

    /**
     * 初始化ViewPager
     */
    public void initViewPager() {
        // 载入图片资源ID
        TypedArray typedArray = getResources().obtainTypedArray(R.array.pictureId);
        int len = typedArray.length();
        int[] pictureId = new int[len];
        for (int i = 0; i < len; i++) {
            pictureId[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();

        // 将圆点加入到ViewGroup中
        indicator = new ImageView[pictureId.length];
        for (int i = 0; i < indicator.length; i++) {
            imageView = new ImageView(this.getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(15, 15));
            indicator[i] = imageView;
            if (i == 0) {
                indicator[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                indicator[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
            // 设置圆点间距
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 3;
            layoutParams.rightMargin = 3;
            viewGroup.addView(imageView, layoutParams);
        }

        // 将图片装载到数组中
        picture = new ImageView[pictureId.length];
        for (int i = 0; i < picture.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            picture[i] = imageView;
            imageView.setBackgroundResource(pictureId[i]);
        }

        // 设置Adapter
        viewPager.setAdapter(new MyAdapter());
        // 设置监听，主要是设置圆点的背景
        viewPager.addOnPageChangeListener(this);
        // 设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem((picture.length) * 100);
        ViewPagerScroller.setViewPagerScrollDuration(viewPager, 1500);
    }

    /**
     * ViewPager适配器
     */
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(picture[position % picture.length]);
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(picture[position % picture.length], 0);
            return picture[position % picture.length];
        }

    }

    /**
     * 设置指示器当前选中的样式
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < indicator.length; i++) {
            if (i == selectItems) {
                indicator[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                indicator[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    /**
     * 初始化GridView
     */
    public void initGridView() {
        // 图标数组
        TypedArray typedArray = getResources().obtainTypedArray(R.array.homePage_GridView_Image);
        int len = typedArray.length();
        int[] gridViewImage = new int[len];
        for (int i = 0; i < len; i++) {
            gridViewImage[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        // 文字数组
        String[] gridViewText = getResources().getStringArray(R.array.homePage_GridView_Text);

        gridView.setAdapter(new GridViewAdapter(getActivity(), gridViewParent, gridViewImage, gridViewText, 2));
        // 注册监听事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(getActivity(), DtuListActivity.class);
                        intent.putExtra("To", getResources().getString(R.string.real_time_monitoring));
                        startActivity(intent);
                        break;

                    case 1:
                        intent.setClass(getActivity(), DtuListActivity.class);
                        intent.putExtra("To", getResources().getString(R.string.historical_data_query));
                        startActivity(intent);
                        break;

                    /*case 2:
                        intent.setClass(getActivity(), DtuListActivity.class);
                        intent.putExtra("To", getResources().getString(R.string.abnormal_data_query));
                        startActivity(intent);
                        break;*/

                    /*case 2:
                        intent.setClass(getActivity(), DtuListActivity.class);
                        intent.putExtra("To", getResources().getString(R.string.abnormal_data_query));
                        startActivity(intent);
                        break;*/
                }
            }
        });
    }

}
