package com.liuwan.mydesign.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.AbnormalData;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;
import com.tencent.tencentmap.mapsdk.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/10/22.
 * 覆盖物绘制工具类
 */
public class OverlayUtil {

    private Context context;
    private TencentMap tencentMap;

    public OverlayUtil(Context context, TencentMap tencentMap) {
        this.context = context;
        this.tencentMap = tencentMap;
    }

    /**
     * 绘制折线
     */
    public Polyline drawPolyline(List<LatLng> latLngList, int lineColor, float lineWidth, boolean gradient) {
        Polyline polyline = tencentMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .width(lineWidth));
        if (gradient) {
            // 渐变着色，需要设置颜色的顶点位置
            int[] indexs = new int[latLngList.size()];
            for (int i = 0; i < indexs.length; i++) {
                indexs[i] = i;
            }

            polyline.setColors(
                    DisplayUtil.gradientColors(context, lineColor, R.color.normal, latLngList.size()), indexs);
        } else {
            // 固定颜色
            polyline.setColor(ContextCompat.getColor(context, lineColor));
        }
        return polyline;
    }

    /**
     * 清除折线
     */
    public void removePolyline(List<Polyline> polylineList) {
        for (int i = 0; i < polylineList.size(); i++) {
            polylineList.get(i).remove();
        }
    }

    /**
     * 计算折线宽度
     */
    public float lineWidth(float zoomLevel) {
        return (float) (0.01234 * Math.exp(0.6393 * zoomLevel));
    }

    /**
     * 绘制河道
     */
    public List<Polyline> drawRiverPath(List<List<LatLng>> lists, float lineWidth) {
        List<Polyline> polylineList = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++) {
            List<LatLng> list = lists.get(i);
            Polyline polyline = drawPolyline(list, R.color.normal, lineWidth, false);
            polylineList.add(polyline);
        }
        return polylineList;
    }

    /**
     * 绘制污染物扩散路径
     */
    public List<Polyline> drawPollutantPath(List<List<List<LatLng>>> pollutantPathLists,
                                            float lineWidth,
                                            List<AbnormalData> abnormalDataList) {
        int[] levelArray = new int[abnormalDataList.size()];
        for (int i = 0; i < abnormalDataList.size(); i++) {
            levelArray[i] = abnormalDataList.get(i).getLevel();
        }

        List<Polyline> polylineList = new ArrayList<>();
        for (int i = 0; i < pollutantPathLists.size(); i++) {
            List<List<LatLng>> pollutantPathList = pollutantPathLists.get(i);
            for (int j = 0; j < pollutantPathList.size(); j++) {
                List<LatLng> pollutantPath = pollutantPathList.get(j);
                int color = 0;
                switch (levelArray[i]) {
                    case 1:
                        color = R.color.level1;
                        break;
                    case 2:
                        color = R.color.level2;
                        break;
                    case 3:
                        color = R.color.level3;
                        break;
                    case 4:
                        color = R.color.level4;
                        break;
                    case 5:
                        color = R.color.level5;
                        break;
                }

                Polyline polyline = drawPolyline(pollutantPath, color, lineWidth, true);
                polylineList.add(polyline);
            }
        }
        return polylineList;
    }

}
