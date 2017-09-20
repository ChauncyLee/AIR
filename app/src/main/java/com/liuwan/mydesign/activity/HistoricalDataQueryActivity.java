package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by liuwan on 2016/11/19.
 * 历史数据查询界面
 */
@SuppressLint("SetTextI18n")
public class HistoricalDataQueryActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout back, filter;
    private TextView selectorCurve, selectorTable, selectedSite, selectedTime;
    private LinearLayout curveChart, table, customCurveChart;
    private TextView legend, range;
    private ListView tableList;
    private TextView tvTotalPage, tvCurrentPage, tvLastPage, tvNextPage;
    private TextView tvMaxValue, tvMaxTime, tvMinValue, tvMinTime, tvAverageValue;
    private LoadingDialog loadingDialog;
    private String site, startTime, endTime, route,r, parameter;;
    private int flag;
    private float[] normalRange;
    private List<ParameterData> parameterDataList;
    private int totalPage, currentPage;
    // 每页显示条目数
    private static final int PAGED_NUMBER = 30;

    int orange= Color.rgb(255,127,36);
    int green=Color.rgb(102,205,0);
    int white=Color.rgb(255,255,255);
    private List<NHData> parameterDataList2;
    private String dId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historicaldataquery);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        filter = (LinearLayout) findViewById(R.id.filter);
        filter.setOnClickListener(this);
        selectedSite = (TextView) findViewById(R.id.selectedSite);
        selectedTime = (TextView) findViewById(R.id.selectedTime);
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
        tvTotalPage = (TextView) findViewById(R.id.tvTotalPage);
        tvCurrentPage = (TextView) findViewById(R.id.tvCurrentPage);
        tvLastPage = (TextView) findViewById(R.id.tvLastPage);
        tvLastPage.setOnClickListener(this);
        tvNextPage = (TextView) findViewById(R.id.tvNextPage);
        tvNextPage.setOnClickListener(this);
        tvMaxValue = (TextView) findViewById(R.id.maxValue);
        tvMaxTime = (TextView) findViewById(R.id.maxTime);
        tvMinValue = (TextView) findViewById(R.id.minValue);
        tvMinTime = (TextView) findViewById(R.id.minTime);
        tvAverageValue = (TextView) findViewById(R.id.averageValue);

        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
    /*    if (!sharedPreferences.contains("SelectedSite")) {
            site = getString(R.string.default_site);
            editor.putString("SelectedSite", site);
        } else {
            site = sharedPreferences.getString("SelectedSite", "");
        }*/

        if (!sharedPreferences.contains("SelectedStartTime") ||
                !sharedPreferences.contains("SelectedEndTime")) {
            // 计算当天日期零点时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            startTime = sdf.format(date);
            endTime = sdf.format(new Date());
            editor.putString("SelectedStartTime", startTime);
            editor.putString("SelectedEndTime", endTime);
        } else {
            startTime = sharedPreferences.getString("SelectedStartTime", "");
            endTime = sharedPreferences.getString("SelectedEndTime", "");
        }
        editor.apply();


        Intent intent = getIntent();
        route = intent.getStringExtra("route");
        parameter = intent.getStringExtra("Parameter");
        dId=intent.getStringExtra("dId");
        site=dId;
        selectedSite.setText("dtu0"+site);
        selectedTime.setText(startTime + " - " + endTime);
        legend.setText(parameter);
        switch (route){
            case "NH3":r="1";
                break;
            case "Atmos":r="2";
                break;
            case "Humidity":r="3";
                break;
            case "Temperature":r="4";
                break;
            case "WindSpeed":r="5";
                break;
            case "Angle":r="6";
                break;
            case "H2S":r="7";
                break;
            case "CO2":r="8";
                break;
            case "CH4":r="9";
                break;

        }
        flag = intent.getIntExtra("Flag", 0);
        //getNormalRange();
        getHistoricalData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.filter:
                Intent intent = new Intent(HistoricalDataQueryActivity.this, ConditionFilterActivity.class);
                intent.putExtra("From", "HistoricalDataQuery");
                intent.putExtra("dId",dId);
                startActivityForResult(intent, 2001);
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

            case R.id.tvLastPage:
                if (currentPage > 1) {
                    currentPage--;
                    tvCurrentPage.setText(currentPage + "");

                    List<ParameterData> pagedDataList = DataAnalysisUtil.
                            pagingForList(parameterDataList, PAGED_NUMBER, currentPage);
                    List<Map<String, String>> list = DataAnalysisUtil.
                            analysisTo2List(pagedDataList);
                    tableList.setAdapter(new TableListViewAdapter(this, list, 2));
                    DisplayUtil.setListViewHeightBasedOnChildren(tableList);
                }
                break;

            case R.id.tvNextPage:
                if (currentPage < totalPage) {
                    currentPage++;
                    tvCurrentPage.setText(currentPage + "");

                    List<ParameterData> pagedDataList = DataAnalysisUtil.
                            pagingForList(parameterDataList, PAGED_NUMBER, currentPage);
                    List<Map<String, String>> list = DataAnalysisUtil.
                            analysisTo2List(pagedDataList);
                    tableList.setAdapter(new TableListViewAdapter(this, list, 2));
                    DisplayUtil.setListViewHeightBasedOnChildren(tableList);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001 && resultCode == 2009) {
            //site = data.getStringExtra("SelectedSite");
            startTime = data.getStringExtra("SelectedStartTime");
            endTime = data.getStringExtra("SelectedEndTime");
            selectedSite.setText(site);
            selectedTime.setText(startTime + " - " + endTime);
            //getNormalRange();
            getHistoricalData();
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


    private void showCurveChart(List<ParameterData> dataList) {
        String[] xLabel;
        String[] yLabel;
        float[] data;
        if (dataList != null) {
            List<Object> list = DataAnalysisUtil.analysisToCurve(dataList);
            xLabel = (String[]) list.get(0);
            for(int i =0;i<xLabel.length;i++)
                Log.i(xLabel[i],"fff");
            yLabel = (String[]) list.get(1);
            data = (float[]) list.get(2);
        } else {
            xLabel = new String[]{""};
            yLabel = new String[]{"0", "1"};
            data = new float[]{0};
        }

        customCurveChart.removeAllViews();
        customCurveChart.addView(new CustomCurveChart(this, xLabel, yLabel, data,
                R.color.application_orange, false));
    }

   /* private void showCurveChart(List<NHData> dataList) {
        String[] xLabel;
        String[] yLabel;
        float[] data2;
        if (dataList != null) {
            List<Object> list = DataAnylisis.analysisToCurve2(dataList);
            xLabel = (String[]) list.get(0);
            for(int i =0;i<xLabel.length;i++)
                Log.i(xLabel[i],"fff");
            yLabel = (String[]) list.get(1);
            data2 = (float[]) list.get(2);
            for(int i =0;i<data2.length;i++)
                Log.i(data2[i]+"","cccc");




            List<String> xValues = new ArrayList<>();
            for (int i = 0; i < xLabel.length; i++) {
                xValues.add(xLabel[i]);
                Log.i(xValues.get(i),"ddd");
            }
            for (int i = 0; i < xLabel.length; i++)
                Log.i("xValues", xValues.get(i));


            LineChart lineChart1 = (LineChart) findViewById(R.id.line_chart1);


            List<ILineDataSet> list_get = new ArrayList<>();
            ArrayList<Entry> yVals = new ArrayList<Entry>();
            for (int i = 0; i <data2.length; i++) {
                yVals.add(new Entry(i, data2[i]));
                Log.i(data2[i]+"","eee");
            }
            Log.i("test16","test1");
            LineDataSet set2 = new LineDataSet(yVals, "");
            set2.setDrawValues(true);
            set2.setDrawValues(false);
            set2.setColor(orange);
            set2.setLineWidth(2);
            set2.setCircleColor(orange);
            set2.setCircleColorHole(orange);
            lineChart1.setDragEnabled(true);
            list_get.add(set2);
            LineData data = new LineData(list_get);
            lineChart1.getXAxis().setDrawGridLines(false);
            lineChart1.getXAxis().setAxisLineColor(green);
            lineChart1.getAxisLeft().setDrawGridLines(false);
            Legend l = lineChart1.getLegend();
            l.setForm(Legend.LegendForm.NONE);
            Log.i("test16","test");
            lineChart1.setData(data);
            Log.i("test17","test");

            final String[] values = new String[10000000];
            for (int i = 0; i < xValues.size(); i++) {
                values[i] = xValues.get(i);
                    Log.i(values[i],"ggg");

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
            yAxisright.setEnabled(false);
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
    }*/
    /**
     * 显示列表数据
     */
    private void showTableList(List<ParameterData> dataList) {
        totalPage = 0;
        currentPage = 0;
        List<Map<String, String>> list = new ArrayList<>();
        if (dataList != null) {
            // 计算总页数
            if (dataList.size() % PAGED_NUMBER == 0) {
                totalPage = dataList.size() / PAGED_NUMBER;
            } else {
                totalPage = dataList.size() / PAGED_NUMBER + 1;
            }
            currentPage = 1;

            List<ParameterData> pagedDataList = DataAnalysisUtil.
                    pagingForList(parameterDataList, PAGED_NUMBER, currentPage);
            list = DataAnalysisUtil.analysisTo2List(pagedDataList);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("Time", "无");
            map.put("Value", "无");
            list.add(map);
        }
        tvTotalPage.setText(totalPage + "");
        tvCurrentPage.setText(currentPage + "");
        tableList.setAdapter(new TableListViewAdapter(this, list, 2));
        DisplayUtil.setListViewHeightBasedOnChildren(tableList);
    }

    /**
     * 显示表格数据
     */
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
   /* private void getNormalRange() {
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

                getHistoricalData();
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
                            Toast.makeText(HistoricalDataQueryActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(HistoricalDataQueryActivity.this, getString(R.string.network_error),
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
    }*/

    /**
     * 获取并显示历史数据
     */
    private void getHistoricalData() {
        RequestParams params = new RequestParams(MyApplication.appIp + "/ParameterandData");
        params.addBodyParameter("site", site);
        params.addBodyParameter("startTime", startTime);
        params.addBodyParameter("endTime", endTime);
        params.addBodyParameter("route", r);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                if (!"[]".equals(result)) {
                    Log.i("result0",result);
                    parameterDataList = DataAnalysisUtil.encapsulateParameterData(result);
                    parameterDataList2 = DataAnylisis.encapsulateNH(result);
                    Log.i("mm1",parameterDataList.get(2).toString());
                    Log.i("mm2",parameterDataList2.get(2).toString());
                    if (parameterDataList.size() == 1) {
                        showCurveChart(null);
                    } else {
                        showCurveChart(parameterDataList);
                    }
                    showTableList(parameterDataList);
                    showTable(parameterDataList);
                } else {
                    Log.i("result1",result);
                    showCurveChart(null);
                    showTableList(null);
                    showTable(null);
                    Toast.makeText(HistoricalDataQueryActivity.this,
                            getResources().getString(R.string.no_data), Toast.LENGTH_LONG).show();
                }
                Log.i("result2",result);

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
                            Toast.makeText(HistoricalDataQueryActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(HistoricalDataQueryActivity.this, getString(R.string.network_error),
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
