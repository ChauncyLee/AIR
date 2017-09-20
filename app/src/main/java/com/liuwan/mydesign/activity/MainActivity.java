package com.liuwan.mydesign.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.fragment.main.HomePageFragment;
import com.liuwan.mydesign.fragment.main.MeFragment;

/**
 * Created by liuwan on 2016/10/24.
 * 主界面
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout homePage, me;
    private ImageView iconHomePage, iconMe;
    private TextView txHomePage, txMe;
    private Fragment homePageFragment, meFragment;
    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        homePage = (LinearLayout) findViewById(R.id.homePage);
        homePage.setOnClickListener(this);
        iconHomePage = (ImageView) findViewById(R.id.iconHomePage);
        txHomePage = (TextView) findViewById(R.id.txHomePage);

        me = (LinearLayout) findViewById(R.id.me);
        me.setOnClickListener(this);
        iconMe = (ImageView) findViewById(R.id.iconMe);
        txMe = (TextView) findViewById(R.id.txMe);

        // 初始化fragment布局
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (homePageFragment == null) {
            homePageFragment = new HomePageFragment();
        }
        transaction.replace(R.id.fragment_main, homePageFragment).commit();
        iconHomePage.setImageResource(R.drawable.homepage_checked);
        iconMe.setImageResource(R.drawable.me_unchecked);
        txHomePage.setTextColor(ContextCompat.getColor(this, R.color.search_box_text));
        txMe.setTextColor(ContextCompat.getColor(this, R.color.application_white));
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (v.getId()) {

            case R.id.homePage:
                if (homePageFragment == null) {
                    homePageFragment = new HomePageFragment();
                }
                transaction.replace(R.id.fragment_main, homePageFragment).commit();
                iconHomePage.setImageResource(R.drawable.homepage_checked);
                iconMe.setImageResource(R.drawable.me_unchecked);
                txHomePage.setTextColor(ContextCompat.getColor(this, R.color.search_box_text));
                txMe.setTextColor(ContextCompat.getColor(this, R.color.application_white));
                break;
            case R.id.me:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                }
                transaction.replace(R.id.fragment_main, meFragment).commit();
                iconHomePage.setImageResource(R.drawable.homepage_unchecked);
                iconMe.setImageResource(R.drawable.me_checked);
                txHomePage.setTextColor(ContextCompat.getColor(this, R.color.application_white));
                txMe.setTextColor(ContextCompat.getColor(this, R.color.search_box_text));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
