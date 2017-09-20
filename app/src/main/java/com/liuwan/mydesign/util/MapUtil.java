package com.liuwan.mydesign.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.activity.ParameterSelectionActivity;
import com.liuwan.mydesign.bean.SiteInfo;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

import java.util.List;

/**
 * Created by liuwan on 2016/10/22.
 * 地图工具类
 */
public class MapUtil {

    private Context context;
    private TencentMap tencentMap;

    public MapUtil(Context context, TencentMap tencentMap) {
        this.context = context;
        this.tencentMap = tencentMap;
        initMap();
    }

    /**
     * 初始化地图
     */
    public void initMap() {
        // 设置卫星地图
        tencentMap.setMapType(TencentMap.MAP_TYPE_SATELLITE);

        // 获取UiSettings实例
        UiSettings uiSettings = tencentMap.getUiSettings();
        // 设置缩放控件隐藏
        uiSettings.setZoomControlsEnabled(false);
        // 设置指南针
        uiSettings.setCompassEnabled(false);
        // 设置定位控件
        uiSettings.setMyLocationButtonEnabled(false);
        // 设置比例尺到屏幕左下角
        uiSettings.setScaleViewPosition(0);
        uiSettings.showScaleView(true);
        // 缩放手势
        uiSettings.setZoomGesturesEnabled(true);
    }

    /**
     * 移动和缩放地图
     */
    public void moveTo(LatLng mapCenter, float zoomLevel) {
        CameraUpdate cameraSigma = CameraUpdateFactory.newCameraPosition(
                // 俯仰角 0~45° (垂直地图时为0f),偏航角 0~360° (正北方为0f)
                new CameraPosition(mapCenter, zoomLevel, 0, 0));
        // 移动地图
        tencentMap.moveCamera(cameraSigma);
    }

    /**
     * 站点标记
     */
    public void mark(List<SiteInfo> siteList) {
        for (int i = 0; i < siteList.size(); i++) {
            double latitude = siteList.get(i).getLatitude();
            double longitude = siteList.get(i).getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            String name = siteList.get(i).getName();

            Marker marker = tencentMap.addMarker(new MarkerOptions(latLng)
                    .title(name)
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .is3D(false)
                    .draggable(false));
            // 显示信息窗
            marker.showInfoWindow();
        }
        // 绑定标注点击事件
        tencentMap.setOnMarkerClickListener(new MyMarkerClickListener());
        // 设置信息窗适配器
        tencentMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        // 绑定信息窗点击事件
        tencentMap.setOnInfoWindowClickListener(new MyInfoWindowClickListener());
    }

    /**
     * 监听标注点击事件
     */
    private class MyMarkerClickListener implements TencentMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            // 腾讯和百度地图API只能显示一个InfoWindow
            // 点击后显示当前Marker的InfoWindow，再通过点击InfoWindow跳转显示相应站点
            return false;
        }

    }

    /**
     * 监听信息窗点击事件
     */
    private class MyInfoWindowClickListener implements TencentMap.OnInfoWindowClickListener {

        @Override
        public void onInfoWindowClick(Marker marker) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyDesign",
                    Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SelectedSiteRT", marker.getTitle());
            editor.apply();
            Intent intent = new Intent(context, ParameterSelectionActivity.class);
            intent.putExtra("To", context.getString(R.string.real_time_monitoring));
            context.startActivity(intent);
        }

    }

    /**
     * 自定义信息窗样式
     */
    private class MyInfoWindowAdapter implements TencentMap.InfoWindowAdapter {

        // 返回View为信息窗按压时的样式
        @Override
        public View getInfoWindowPressState(Marker arg0) {
            LinearLayout infowindow = (LinearLayout) View.inflate(context, R.layout.custom_infowindow, null);
            TextView tvTitle = (TextView) infowindow.findViewById(R.id.tv_title);
            tvTitle.setText(arg0.getTitle());
            return infowindow;
        }

        // 返回View为信息窗自定义样式
        @Override
        public View getInfoWindow(final Marker arg0) {
            LinearLayout infowindow = (LinearLayout) View.inflate(context, R.layout.custom_infowindow, null);
            TextView tvTitle = (TextView) infowindow.findViewById(R.id.tv_title);
            tvTitle.setText(arg0.getTitle());
            return infowindow;
        }

        // 返回View为信息窗内容自定义样式
        @Override
        public View getInfoContents(Marker arg0) {
            return null;
        }

    }

}
