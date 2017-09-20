package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.liuwan.mydesign.R;
import com.liuwan.mydesign.adapter.TableListViewAdapter;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.bean.NHData;
import com.liuwan.mydesign.bean.ParameterData;
import com.liuwan.mydesign.util.DataAnalysisUtil;
import com.liuwan.mydesign.util.DisplayUtil;
import com.liuwan.mydesign.widget.CustomCurveChart;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liuwan on 2016/11/19.
 * 实时监测界面
 */
public class RealTimeMonitoringActivity extends FragmentActivity implements View.OnClickListener {

    int orange= Color.rgb(255,127,36);
    int green=Color.rgb(102,205,0);
    int white=Color.rgb(255,255,255);

    private LinearLayout back, filter;
    private TextView selectorCurve, selectorTable, selectedSite;
    private LinearLayout curveChart, table, customCurveChart;
    private TextView legend, range;
    private ListView tableList;
    private TextView tvMaxValue, tvMaxTime, tvMinValue, tvMinTime, tvAverageValue;
    private LoadingDialog loadingDialog;
    private String site, parameter;
    private float[] normalRange;
    private List<ParameterData> parameterDataList;
    private List<NHData> parameterDataList2;
    private String dId="";
    private String route="";

    /**
     * 计时线程
     */
    private Timer timer;

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
                getRealTimeData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtimemonitoring);


        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        filter = (LinearLayout) findViewById(R.id.filter);
        filter.setOnClickListener(this);
        selectedSite = (TextView) findViewById(R.id.selectedSite);
        selectorCurve = (TextView) findViewById(R.id.selectorCurve);
        selectorCurve.setOnClickListener(this);
        selectorTable = (TextView) findViewById(R.id.selectorTable);
        selectorTable.setOnClickListener(this);
        curveChart = (LinearLayout) findViewById(R.id.curveChart);
        legend = (TextView) findViewById(R.id.legend);
        range = (TextView) findViewById(R.id.range);
        table = (LinearLayout) findViewById(R.id.table);
        customCurveChart = (LinearLayout) findViewById(R.id.customCurveChart);
        tableList = (ListView) findViewById(R.id.tableList);
        tableList.setFocusable(false);
        tvMaxValue = (TextView) findViewById(R.id.maxValue);
        tvMaxTime = (TextView) findViewById(R.id.maxTime);
        tvMinValue = (TextView) findViewById(R.id.minValue);
        tvMinTime = (TextView) findViewById(R.id.minTime);
        tvAverageValue = (TextView) findViewById(R.id.averageValue);

        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        if (!sharedPreferences.contains("SelectedSiteRT")) {
            site = getString(R.string.default_site);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SelectedSiteRT", site);
            editor.apply();
        } else {
            site = sharedPreferences.getString("SelectedSiteRT", "");
        }
        selectedSite.setText(site);

        Intent intent = getIntent();
        parameter = intent.getStringExtra("Parameter");
        dId = intent.getStringExtra("dId");
        route=intent.getStringExtra("route");
        legend.setText(parameter);
        timer = null;
        timer = new Timer();
        timer.schedule(new MyTimerTask(), 0, 20000);
        //getNormalRange();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.filter:
                Intent intent = new Intent(RealTimeMonitoringActivity.this, SearchSiteActivity.class);
                intent.putExtra("From", "RealTimeMonitoring");
                startActivityForResult(intent, 1001);
                break;

            case R.id.selectorCurve:
                selectorCurve.setTextColor(ContextCompat.getColor(this, R.color.application_white));
                selectorCurve.setBackgroundColor(ContextCompat.getColor(this, R.color.application_green));
                selectorTable.setTextColor(ContextCompat.getColor(this, R.color.application_text_gray));
                selectorTable.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_border));
                curveChart.setVisibility(View.VISIBLE);
                table.setVisibility(View.GONE);
                break;



            case R.id.selectorTable:
                selectorCurve.setTextColor(ContextCompat.getColor(this, R.color.application_text_gray));
                selectorCurve.setBackground(ContextCompat.getDrawable(this, R.drawable.textview_border));
                selectorTable.setTextColor(ContextCompat.getColor(this, R.color.application_white));
                selectorTable.setBackgroundColor(ContextCompat.getColor(this, R.color.application_green));
                curveChart.setVisibility(View.GONE);
                table.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == 1009) {
            site = data.getStringExtra("SelectedSiteRT");
            selectedSite.setText(site);
            if (timer != null) {
                timer.cancel();
            }
            //getNormalRange();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 显示曲线图数据
     */
    private void showCurveChart(List<NHData> dataList) {
        String[] xLabel;
        String[] yLabel;
        float[] data2;
        if (dataList != null) {
            List<Object> list = DataAnylisis.analysisToCurve2(dataList);
            xLabel = (String[]) list.get(0);
            for(int i =0;i<xLabel.length;i++)
                Log.i(xLabel[i],"xLabel");
            yLabel = (String[]) list.get(1);
            data2 = (float[]) list.get(2);
            for(int i =0;i<data2.length;i++)
                Log.i(data2[i]+"","bbb");

            for (int i = 0; i < yLabel.length; i++)
                Log.i("data", yLabel[i]);

            /**
             *解析x轴所需数据
             */
            List<String> xValues = new ArrayList<>();
            for (int i = 0; i < xLabel.length; i++) {
                xValues.add(xLabel[i]);
            }
            for (int i = 0; i < xLabel.length; i++)
                Log.i("xValues", xValues.get(i));

            /**
             * 解析y轴所需数据
             */
            List<String> yValues = new ArrayList<>();
            for (int i = 0; i < yLabel.length; i++) {
                yValues.add(yLabel[i]);
                Log.i("yValues", yValues.get(0));
            }
            LineChart lineChart1 = (LineChart) findViewById(R.id.line_chart1);

            for (int i = 0; i < yValues.size(); i++)
                Log.i("yValues", yValues.get(i));
            /**
             * 设置y轴数据
             */
            List<ILineDataSet> list_get = new ArrayList<>();
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i = 0; i <xLabel.length; i++) {
                yVals.add(new Entry(i, data2[i]));
            }

            LineDataSet set1 = new LineDataSet(yVals, "");
            set1.setDrawValues(true);
            set1.setDrawValues(false);
            set1.setColor(orange);
            set1.setLineWidth(2);
            set1.setCircleColor(orange);
            set1.setCircleColorHole(orange);
            lineChart1.setDragEnabled(true);
            list_get.add(set1);
            LineData data = new LineData(list_get);
            lineChart1.getXAxis().setDrawGridLines(false);
            lineChart1.getXAxis().setAxisLineColor(green);
            lineChart1.getAxisLeft().setDrawGridLines(false);
            Legend l = lineChart1.getLegend();
            l.setForm(Legend.LegendForm.NONE);

            lineChart1.setData(data);


            /**
             * 设置x轴数据
             */

            final String[] values = new String[100];
            for (int i = 0; i < xValues.size(); i++) {
                values[i] = xValues.get(i);
                Log.i(values[i],"vvv");
            }
            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return values[(int) value ];
                }

            };

            XAxis xAxis = lineChart1.getXAxis();
            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
            xAxis.setValueFormatter(formatter);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setAxisLineColor(green);
            xAxis.setAxisLineWidth(2);

            YAxis yAxisright = lineChart1.getAxisRight();
            /*yAxisright.setEnabled(false);*/
            yAxisright.setDrawAxisLine(true);
            yAxisright.setDrawGridLines(false);
            yAxisright.setTextColor(Color.TRANSPARENT);
            yAxisright.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            yAxisright.setAxisLineColor(white);
            YAxis yAxis = lineChart1.getAxisLeft();
            yAxis.setAxisLineColor(green);
            yAxisright.setXOffset(15f);
            yAxis.setAxisLineWidth(2);
            lineChart1.invalidate();
        }
    }

    /**
     * 显示列表数据
     */
    private void showTableList(List<ParameterData> dataList) {
        List<Map<String, String>> list = new ArrayList<>();
        if (dataList != null) {
            list = DataAnalysisUtil.analysisTo2List(parameterDataList);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("Time", "无");
            map.put("Value", "无");
            list.add(map);
        }
        tableList.setAdapter(new TableListViewAdapter(this, list, 2));
        DisplayUtil.setListViewHeightBasedOnChildren(tableList);
    }

    /**
     * 显示表格数据
     */
    @SuppressLint("SetTextI18n")
    private void showTable(List<ParameterData> dataList) {
        List<Object> list = new ArrayList<>();
        if (dataList != null) {
            list = DataAnalysisUtil.analysisToTable(dataList);
        } else {
            for (int i = 0; i < 5; i++) {
                list.add("无");
            }
        }
        tvMaxValue.setText(list.get(0) + "");
        tvMaxTime.setText(list.get(1) + "");
        tvMinValue.setText(list.get(2) + "");
        tvMinTime.setText(list.get(3) + "");
        tvAverageValue.setText(list.get(4) + "");
    }

    /**
     * 获取参数的正常值的范围
     */
    /*private void getNormalRange() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/RangeOfParameter");
        params.addBodyParameter("Parameter", parameter);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                normalRange = DataAnalysisUtil.encapsulateNormalRange(result);
                String rangeStr = "";
                if (normalRange[0] != 0 && normalRange[1] == 0) {
                    // 没有上限
                    rangeStr = "(" + ">=" + normalRange[0] + ")";
                } else if (normalRange[0] == 0 && normalRange[1] != 0) {
                    // 没有下限
                    rangeStr = "(" + "<=" + normalRange[1] + ")";
                } else if (normalRange[0] != 0) {
                    // 一定范围
                    rangeStr = "(" + normalRange[0] + "~" + normalRange[1] + ")";
                }
                range.setText(rangeStr);
                timer = null;
                timer = new Timer();
                timer.schedule(new MyTimerTask(), 0, 20000);
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Log.e("A",errorInfo);
                            Toast.makeText(RealTimeMonitoringActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(RealTimeMonitoringActivity.this, getString(R.string.network_error),
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
*/
    /**
     * 获取并显示实时数据
     */
    private void getRealTimeData() {
        RequestParams params = new RequestParams(MyApplication.appIp + "/"+route);
        params.addBodyParameter("Number","12");
        params.addBodyParameter("Mid", dId);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("aaa", result);
                if (!"[]".equals(result)) {
                    parameterDataList = DataAnalysisUtil.encapsulateParameterData(result);
                    parameterDataList2 = DataAnylisis.encapsulateNH(result);
                    Log.i("m1",parameterDataList.get(2).toString());
                    Log.i("m2",parameterDataList2.get(2).toString());
                    if (parameterDataList.size() == 1) {
                        showCurveChart(null);
                    } else {
                        showCurveChart(parameterDataList2);
                    }
                    showTableList(parameterDataList);
                    showTable(parameterDataList);
                } else {
                    showCurveChart(null);
                    showTableList(null);
                    showTable(null);
                    Toast.makeText(RealTimeMonitoringActivity.this,
                            getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
                }

                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(RealTimeMonitoringActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(RealTimeMonitoringActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                    Log.i("aaa", "网络异常");
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
