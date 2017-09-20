package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.AbnormalData;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.bean.ParameterInfo;
import com.liuwan.mydesign.bean.RiverNodeInfo;
import com.liuwan.mydesign.bean.SiteInfo;
import com.liuwan.mydesign.util.DataAnalysisUtil;
import com.liuwan.mydesign.util.MapUtil;
import com.liuwan.mydesign.util.OverlayUtil;
import com.liuwan.mydesign.util.PollutionDiffusionUtil;
import com.liuwan.mydesign.widget.LoadingDialog;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuwan on 2016/10/22.
 * 地图预警界面
 */
public class MapWarningActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private LoadingDialog loadingDialog1, loadingDialog2, loadingDialog3, loadingDialog4, loadingDialog5;
    private MapView mapView;
    private TencentMap tencentMap;
    private MapUtil mapUtil;
    private OverlayUtil overlayUtil;

    // 监测站点集合
    private List<SiteInfo> siteList;
    // 河道坐标集合
    private List<List<LatLng>> riverPathList;
    // 河道覆盖物对象集合
    private List<Polyline> riverPolylineList;
    // 污染物信息集合，包括污染物正常值范围，降解系数
    private List<ParameterInfo> parameterList;
    // 河道结点集合
    private List<RiverNodeInfo> riverNodeList;
    private Integer[] idArray;
    // 河道结点关系，邻接矩阵
    private double[][] riverNodeRelation;

    // 所有站点的所有污染物的异常值的对象的集合
    private List<AbnormalData> abnormalDataList;
    // 污染物扩散的起点
    private List<Integer> startPositionList;
    // 染物降解到正常值时的扩散距离
    private List<Double> distanceList;
    // 污染扩散路径坐标集合（第一层：多个站点的污染扩散路径；第二层：污染扩散路径可能有分支；第三层：结点id）
    private List<List<List<Integer>>> idPathLists;
    private List<List<List<LatLng>>> pollutantPathLists;
    // 污染物扩散路径覆盖物对象集合
    private List<Polyline> pollutantPolylineList;

    // 初始化地图中心
    private LatLng center = new LatLng(32.068611, 118.806839);
    // 初始化缩放系数
    private float zoomLevel = 10.0f;
    // 初始化河道宽度
    private float lineWidth = 10.0f;

    /**
     * 计时线程
     */
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {

        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = 1;
            handler.sendMessage(msg);
        }

    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                updateLastedParameterInfo();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapwarning);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);

        // 初始化地图
        mapView = (MapView) findViewById(R.id.mapView);
        // 获取TencentMap实例
        tencentMap = mapView.getMap();
        tencentMap.setOnCameraChangeListener(new MyCameraChangeListener());
        mapUtil = new MapUtil(this, tencentMap);
        overlayUtil = new OverlayUtil(this, tencentMap);

        mapUtil.moveTo(center, zoomLevel);

        getSiteInfo();
        getRiverWay();
        getDegradationCoefficient();
        getRiverNodeInfo();
        getRiverNodeRelation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (task != null) {
            task.cancel();
        }
    }

    /**
     * 当地图发生变化时的调用接口
     */
    private class MyCameraChangeListener implements TencentMap.OnCameraChangeListener {

        // 地图正在发生变化
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            if (zoomLevel != cameraPosition.zoom) {
                zoomLevel = cameraPosition.zoom;
                if (zoomLevel < 10.0f) {
                    zoomLevel = 10.0f;
                } else if (zoomLevel > 14.0f) {
                    zoomLevel = 14.0f;
                }
                CameraUpdate cameraSigma = CameraUpdateFactory.zoomTo(zoomLevel);
                tencentMap.moveCamera(cameraSigma);
            }
        }

        // 地图变化结束
        @Override
        public void onCameraChangeFinished(CameraPosition cameraPosition) {
            // 计算折线宽度
            lineWidth = overlayUtil.lineWidth(zoomLevel);
            // 重绘覆盖物
            if (riverPolylineList != null) {
                overlayUtil.removePolyline(riverPolylineList);
                riverPolylineList = overlayUtil.drawRiverPath(riverPathList, lineWidth);
            }
            // 清除当前绘制的污染物扩散路径
            if (pollutantPolylineList != null) {
                overlayUtil.removePolyline(pollutantPolylineList);
                // 渐变着色绘制污染物扩散路径
                pollutantPolylineList = overlayUtil.
                        drawPollutantPath(pollutantPathLists, lineWidth, abnormalDataList);
            }
        }

    }

    /**
     * 判断所有接口调用结束，开启计时线程获取污染数据
     */
    private void isAllLoadingDialogDismiss() {
        if (loadingDialog1 != null && !loadingDialog1.isShowing() &&
                loadingDialog2 != null && !loadingDialog2.isShowing() &&
                loadingDialog3 != null && !loadingDialog3.isShowing() &&
                loadingDialog4 != null && !loadingDialog4.isShowing() &&
                loadingDialog5 != null && !loadingDialog5.isShowing()) {
            // 启动计时器，定时更新监测到的污染数据
            timer.schedule(task, 1000, 30000);
        }
    }

    /**
     * 获取所有监控站点信息并在地图上绘制
     */
    private void getSiteInfo() {
        loadingDialog1 = new LoadingDialog(this);
        loadingDialog1.setCancelable(false);
        loadingDialog1.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/WarterMonitor");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                siteList = new ArrayList<>();
                try {
                    // 解析数据封装成集合
                    JSONArray sites = new JSONArray(result);
                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject site = sites.getJSONObject(i);
                        String name = site.getString("Name");
                        double latitude = Double.parseDouble(site.getString("Longitude"));
                        double longitude = Double.parseDouble(site.getString("Latitude"));
                        SiteInfo siteInfo = new SiteInfo(name, latitude, longitude);
                        siteList.add(siteInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 绘制监测站点
                mapUtil.mark(siteList);
                mapUtil.moveTo(center, zoomLevel);

                if (loadingDialog1 != null) {
                    loadingDialog1.dismiss();
                }
                isAllLoadingDialogDismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog1 != null) {
                    loadingDialog1.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

    /**
     * 获取河道信息并绘制出河道
     */
    private void getRiverWay() {
        loadingDialog2 = new LoadingDialog(this);
        loadingDialog2.setCancelable(false);
        loadingDialog2.show();
        RequestParams params = new RequestParams(MyApplication.appIp + "/WaterNodeRelation");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                riverPathList = new ArrayList<>();
                try {
                    // 解析数据封装成集合
                    JSONArray nodes = new JSONArray(result);
                    int num = 1;
                    for (int i = 0; i < nodes.length(); i++) {
                        List<LatLng> list = new ArrayList<>();
                        for (int j = i; j < nodes.length(); j++) {
                            JSONObject node = nodes.getJSONObject(j);
                            int group = node.getInt("Group");
                            if (num != group) {
                                i--;
                                break;
                            }
                            double latitude = Double.parseDouble(node.getString("Longitude"));
                            double longitude = Double.parseDouble(node.getString("Latitude"));
                            list.add(new LatLng(latitude, longitude));
                            i++;
                        }
                        riverPathList.add(list);
                        num++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 逆序排列
                Collections.reverse(riverPathList);
                // 绘制河道
                riverPolylineList = overlayUtil.drawRiverPath(riverPathList, lineWidth);

                if (loadingDialog2 != null) {
                    loadingDialog2.dismiss();
                }
                isAllLoadingDialogDismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog2 != null) {
                    loadingDialog2.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

    /**
     * 获取所有污染物的正常值范围、降解系数
     */
    private void getDegradationCoefficient() {
        loadingDialog3 = new LoadingDialog(this);
        loadingDialog3.setCancelable(false);
        loadingDialog3.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/AllParameterTypeDegradation");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                parameterList = new ArrayList<>();
                try {
                    // 解析数据封装成集合
                    JSONArray array = new JSONArray(result);
                    // 预先设定容器的大小，方便在指定位置放入元素
                    for (int j = 0; j < array.length() - 1; j++) {
                        parameterList.add(null);
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        String name = json.getString("Name");
                        double lower = Double.parseDouble(json.getString("LimitValue"));
                        double upper = Double.parseDouble(json.getString("ValueCeiling"));
                        double degradation = Double.parseDouble(json.getString("DegradationFactors"));
                        ParameterInfo parameterInfo = new ParameterInfo(name, lower, upper, degradation);
                        if (getResources().getString(R.string.temperature).equals(name)) {
                            parameterList.set(0, parameterInfo);
                        } else if (getResources().getString(R.string.ph).equals(name)) {
                            parameterList.set(1, parameterInfo);
                        } else if (getResources().getString(R.string.dissolved_oxygen).equals(name)) {
                            parameterList.set(2, parameterInfo);
                        } else if (getResources().getString(R.string.turbidity).equals(name)) {
                            parameterList.set(3, parameterInfo);
                        } else if (getResources().getString(R.string.conductivity).equals(name)) {
                            parameterList.set(4, parameterInfo);
                        } else if (getResources().getString(R.string.blue_green_algae).equals(name)) {
                            parameterList.set(5, parameterInfo);
                        } else if (getResources().getString(R.string.chlorophyll).equals(name)) {
                            parameterList.set(6, parameterInfo);
                        } else if (getResources().getString(R.string.ammonia_nitrogen).equals(name)) {
                            parameterList.set(7, parameterInfo);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (loadingDialog3 != null) {
                    loadingDialog3.dismiss();
                }
                isAllLoadingDialogDismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog3 != null) {
                    loadingDialog3.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

    /**
     * 获取所有河流结点的信息
     */
    private void getRiverNodeInfo() {
        loadingDialog4 = new LoadingDialog(this);
        loadingDialog4.setCancelable(false);
        loadingDialog4.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Id");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                riverNodeList = new ArrayList<>();
                try {
                    // 解析数据封装成集合
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = array.getJSONObject(i);
                        int id = json.getInt("Id");
                        double latitude = Double.parseDouble(json.getString("Longitude"));
                        double longitude = Double.parseDouble(json.getString("Latitude"));
                        RiverNodeInfo riverNodeInfo = new RiverNodeInfo(id, latitude, longitude);
                        riverNodeList.add(riverNodeInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 计算河网顶点id数组
                idArray = new Integer[riverNodeList.size()];
                for (int i = 0; i < idArray.length; i++) {
                    idArray[i] = riverNodeList.get(i).getId();
                }

                if (loadingDialog4 != null) {
                    loadingDialog4.dismiss();
                }
                isAllLoadingDialogDismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog4 != null) {
                    loadingDialog4.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

    /**
     * 获取所有河流结点的邻接矩阵
     */
    private void getRiverNodeRelation() {
        loadingDialog5 = new LoadingDialog(this);
        loadingDialog5.setCancelable(false);
        loadingDialog5.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/WarterRelation");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    // 解析数据封装成集合
                    JSONArray arrays = new JSONArray(result);
                    riverNodeRelation = new double[arrays.length()][arrays.length()];
                    for (int i = 0; i < arrays.length(); i++) {
                        JSONArray array = arrays.getJSONArray(i);
                        for (int j = 0; j < array.length(); j++) {
                            riverNodeRelation[i][j] = array.getDouble(j);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (loadingDialog5 != null) {
                    loadingDialog5.dismiss();
                }
                isAllLoadingDialogDismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog5 != null) {
                    loadingDialog5.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

    /**
     * 更新监测到的污染物数据并在地图上重绘
     */
    private void updateLastedParameterInfo() {
        RequestParams params = new RequestParams(MyApplication.appIp + "/AllRecentNode");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // 解析污染超标数据并确定超标等级
                abnormalDataList = DataAnalysisUtil.
                        encapsulateAbnormalData(MapWarningActivity.this, result);
                Log.i("aaa", abnormalDataList + "");
                // 确定污染物扩散的起点,即监测到污染的站点在河道中位置的数组
                startPositionList = PollutionDiffusionUtil.
                        analysisStartPosition(abnormalDataList, siteList, riverNodeList);
                // 计算污染物降解到正常值时的扩散距离
                distanceList = PollutionDiffusionUtil.
                        analysisDistance(MapWarningActivity.this, abnormalDataList, parameterList);
                Log.i("aaa", distanceList + "");
                // 计算污染扩散的路径的id
                idPathLists = PollutionDiffusionUtil.
                        analysisPollutionPath(idArray, riverNodeRelation, startPositionList, distanceList);
                Log.i("aaa", idPathLists + "");
                // 转换为坐标
                pollutantPathLists = PollutionDiffusionUtil.analysisIdToLatLng(riverNodeList, idPathLists);
                Log.i("aaa", pollutantPathLists + "");
                // 清除当前绘制的污染物扩散路径
                if (pollutantPolylineList != null) {
                    overlayUtil.removePolyline(pollutantPolylineList);
                }
                // 渐变着色绘制污染物扩散路径
                pollutantPolylineList = overlayUtil.
                        drawPollutantPath(pollutantPathLists, lineWidth, abnormalDataList);
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(MapWarningActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MapWarningActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(CancelledException e) {
            }

            @Override
            public void onFinished() {
            }

        });
    }

}