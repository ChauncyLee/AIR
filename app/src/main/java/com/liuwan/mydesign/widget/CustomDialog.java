package com.liuwan.mydesign.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.liuwan.mydesign.util.DisplayUtil;

/**
 * Created by liuwan on 2016/11/3.
 * 自定义对话框
 */
public class CustomDialog extends Dialog {

    public CustomDialog(Context context, double widthScale, double heightScale, View layout, int style) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        int windowWidth = DisplayUtil.getScreenSize(context)[0];
        int windowHeight = DisplayUtil.getScreenSize(context)[1];
        params.width = (int) (windowWidth * widthScale);
        if (heightScale == 0) {
            params.gravity = Gravity.CENTER;
        } else {
            params.gravity = Gravity.TOP;
            params.y = (int) (windowHeight * heightScale);
        }
        window.setAttributes(params);
    }

}