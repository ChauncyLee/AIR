package com.liuwan.mydesign.util;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.liuwan.mydesign.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/11/5.
 * 屏幕显示工具类
 */
public class DisplayUtil {

    /**
     * 获取屏幕大小
     */
    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    /**
     * ScrollView嵌套ListView时动态设置ListView高度全部展开
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null) {
            return;
        }
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 精确到小数点后三位小数
     */
    public static String decimal(float decimal) {
        // 如果小数不足3位，会以0补足
        DecimalFormat decimalFormat = new DecimalFormat("##0.000");
        return decimalFormat.format(decimal);
    }

    /**
     * 颜色int值转十六进制字符串
     */
    public static String colorChangeToString(Context context, int color) {
        String colorHex = "";
        switch (color) {
            case R.color.normal:
                colorHex = context.getResources().getString(R.string.normalColor);
                break;

            case R.color.level1:
                colorHex = context.getResources().getString(R.string.level1Color);
                break;

            case R.color.level2:
                colorHex = context.getResources().getString(R.string.level2Color);
                break;

            case R.color.level3:
                colorHex = context.getResources().getString(R.string.level3Color);
                break;

            case R.color.level4:
                colorHex = context.getResources().getString(R.string.level4Color);
                break;

            case R.color.level5:
                colorHex = context.getResources().getString(R.string.level5Color);
                break;
        }
        return colorHex;
    }

    /**
     * 计算渐变色数组
     */
    public static int[] gradientColors(Context context, int startColor, int endColor, int length) {
        String startColorHex = colorChangeToString(context, startColor);
        String endColorHex = colorChangeToString(context, endColor);
        int r1 = Integer.parseInt(startColorHex.substring(0, 2), 16);
        int g1 = Integer.parseInt(startColorHex.substring(2, 4), 16);
        int b1 = Integer.parseInt(startColorHex.substring(4, 6), 16);
        int r2 = Integer.parseInt(endColorHex.substring(0, 2), 16);
        int g2 = Integer.parseInt(endColorHex.substring(2, 4), 16);
        int b2 = Integer.parseInt(endColorHex.substring(4, 6), 16);
        int d1 = (r1 - r2) / length;
        int d2 = (g1 - g2) / length;
        int d3 = (b1 - b2) / length;
        List<Integer> colorList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            String r = Integer.toHexString(r1 - d1 * i);
            String g = Integer.toHexString(g1 - d2 * i);
            String b = Integer.toHexString(b1 - d3 * i);
            int color = Color.parseColor("#" + r + g + b);
            colorList.add(color);
        }
        Integer[] integer = colorList.toArray(new Integer[colorList.size()]);
        int[] colorArray = new int[colorList.size()];
        for (int i = 0; i < integer.length; i++) {
            colorArray[i] = integer[i];
        }
        return colorArray;
    }

}
