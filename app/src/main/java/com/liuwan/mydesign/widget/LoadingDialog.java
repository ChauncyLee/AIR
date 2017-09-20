package com.liuwan.mydesign.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.liuwan.mydesign.R;

/**
 * Created by liuwan on 2016/11/3.
 * 自定义等待进度条
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
    }

}