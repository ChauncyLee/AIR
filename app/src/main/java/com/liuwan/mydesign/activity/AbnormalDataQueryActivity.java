package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.adapter.TableListViewAdapter;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.bean.ParameterData;
import com.liuwan.mydesign.util.DataAnalysisUtil;
import com.liuwan.mydesign.util.DisplayUtil;
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
 * 异常数据查询界面
 */
@SuppressLint("SetTextI18n")
public class AbnormalDataQueryActivity extends FragmentActivity implements View.OnClickListener {

    private LinearLayout back, filter;
    private TextView selectedSite, selectedTime, legend;
    private ListView tableList;
    private TextView tvTotalPage, tvCurrentPage, tvLastPage, tvNextPage;
    private TextView tvMaxValue, tvMaxTime, tvMinValue, tvMinTime, tvAverageValue;
    private LoadingDialog loadingDialog;
    private String site, startTime, endTime, parameter;
    private float[] normalRange;
    private List<ParameterData> parameterDataList;
    private int totalPage, currentPage;
    // 每页显示条目数
    private static final int PAGED_NUMBER = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abnormaldataquery);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        filter = (LinearLayout) findViewById(R.id.filter);
        filter.setOnClickListener(this);
        selectedSite = (TextView) findViewById(R.id.selectedSite);
        selectedTime = (TextView) findViewById(R.id.selectedTime);
        legend = (TextView) findViewById(R.id.legend);
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
        if (!sharedPreferences.contains("SelectedSiteEx")) {
            site = getString(R.string.default_site);
            editor.putString("SelectedSiteEx", site);
        } else {
            site = sharedPreferences.getString("SelectedSiteEx", "");
        }
        if (!sharedPreferences.contains("SelectedStartTimeEx") ||
                !sharedPreferences.contains("SelectedEndTimeEx")) {
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
            editor.putString("SelectedStartTimeEx", startTime);
            editor.putString("SelectedEndTimeEx", endTime);
        } else {
            startTime = sharedPreferences.getString("SelectedStartTimeEx", "");
            endTime = sharedPreferences.getString("SelectedEndTimeEx", "");
        }
        editor.apply();
        selectedSite.setText(site);
        selectedTime.setText(startTime + " - " + endTime);

        Intent intent = getIntent();
        parameter = intent.getStringExtra("Parameter");
        legend.setText(parameter);
        getNormalRange();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.filter:
                Intent intent = new Intent(AbnormalDataQueryActivity.this, ConditionFilterActivity.class);
                intent.putExtra("From", "ExceptionDataQuery");
                startActivityForResult(intent, 3001);
                break;

            case R.id.tvLastPage:
                if (currentPage > 1) {
                    currentPage--;
                    tvCurrentPage.setText(currentPage + "");
                    List<ParameterData> pagedDataList = DataAnalysisUtil.
                            pagingForList(parameterDataList, PAGED_NUMBER, currentPage);
                    List<Map<String, String>> list = DataAnalysisUtil.
                            analysisTo4List(pagedDataList, normalRange);
                    tableList.setAdapter(new TableListViewAdapter(this, list, 4));
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
                            analysisTo4List(pagedDataList, normalRange);
                    tableList.setAdapter(new TableListViewAdapter(this, list, 4));
                    DisplayUtil.setListViewHeightBasedOnChildren(tableList);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3001 && resultCode == 3009) {
            site = data.getStringExtra("SelectedSiteEx");
            startTime = data.getStringExtra("SelectedStartTimeEx");
            endTime = data.getStringExtra("SelectedEndTimeEx");
            selectedSite.setText(site);
            selectedTime.setText(startTime + " - " + endTime);
            getNormalRange();
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
            list = DataAnalysisUtil.analysisTo4List(pagedDataList, normalRange);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("Time", "无");
            map.put("Value", "无");
            map.put("NormalRange", normalRange[0] + "-" + normalRange[1]);
            map.put("OverProof", "无");
            list.add(map);
        }
        tvTotalPage.setText(totalPage + "");
        tvCurrentPage.setText(currentPage + "");
        tableList.setAdapter(new TableListViewAdapter(this, list, 4));
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
    private void getNormalRange() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/RangeOfParameter");
        params.addBodyParameter("Parameter", parameter);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                normalRange = DataAnalysisUtil.encapsulateNormalRange(result);
                getExceptionData();
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
                            Toast.makeText(AbnormalDataQueryActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(AbnormalDataQueryActivity.this, getString(R.string.network_error),
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
     * 获取并显示异常数据
     */
    private void getExceptionData() {
        RequestParams params = new RequestParams(MyApplication.appIp + "/AbnormalData");
        params.addBodyParameter("Monitor", site);
        params.addBodyParameter("BeginTime", startTime);
        params.addBodyParameter("EndTime", endTime);
        params.addBodyParameter("Parameter", parameter);

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                if (!"[]".equals(result)) {
                    parameterDataList = DataAnalysisUtil.encapsulateParameterData(result);
                    showTableList(parameterDataList);
                    showTable(parameterDataList);
                } else {
                    showTableList(null);
                    showTable(null);
                    Toast.makeText(AbnormalDataQueryActivity.this,
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
                            Toast.makeText(AbnormalDataQueryActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(AbnormalDataQueryActivity.this, getString(R.string.network_error),
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