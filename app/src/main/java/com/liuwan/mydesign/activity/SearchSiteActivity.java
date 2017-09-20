package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.CustomDialog;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/11/6.
 * 站点查询选择界面
 */
public class SearchSiteActivity extends Activity implements View.OnClickListener {

    private LinearLayout back, empty;
    private EditText search;
    private ListView listView;
    private List<String> siteList = new ArrayList<>();
    private List<String> resultList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private LoadingDialog loadingDialog;
    private CustomDialog customDialog;
    private TextView prompt;
    private String siteName, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchsite);

        Intent intent = getIntent();
        from = intent.getStringExtra("From");

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        empty = (LinearLayout) findViewById(R.id.empty);
        empty.setOnClickListener(this);

        search = (EditText) findViewById(R.id.search);
        search.addTextChangedListener(new MyTextWatcher());
        listView = (ListView) findViewById(R.id.listView);

        getSiteInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.empty:
                search.setText("");
                break;

            case R.id.cancel:
                customDialog.dismiss();
                break;

            case R.id.commit:
                Intent backIntent = new Intent();
                if ("RealTimeMonitoring".equals(from)) {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("SelectedSiteRT", siteName);
                    editor.apply();
                    backIntent.setClass(SearchSiteActivity.this, RealTimeMonitoringActivity.class);
                    backIntent.putExtra("SelectedSiteRT", siteName);
                } else if ("ConditionFilter".equals(from)) {
                    backIntent.setClass(SearchSiteActivity.this, ConditionFilterActivity.class);
                    backIntent.putExtra("SelectedSite", siteName);
                }
                setResult(1009, backIntent);
                customDialog.dismiss();
                this.finish();
                break;
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
     * 初始化ListView
     */
    @SuppressLint("SetTextI18n")
    private void initListView() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                siteName = resultList.get(position);
                prompt.setText("确定选择站点 " + siteName + " 吗？");
                customDialog.show();
            }
        });

        resultList.addAll(siteList);
        adapter = new ArrayAdapter<>(this, R.layout.listview_searchsite, resultList);
        listView.setAdapter(adapter);
    }

    /**
     * 初始化Dialog
     */
    @SuppressLint("InflateParams")
    private void initDialog() {
        View submitDelete_view = LayoutInflater.from(this).inflate(R.layout.dialog_confirm, null);
        customDialog = new CustomDialog(this, 0.83, 0.22, submitDelete_view, R.style.customDialogStyle);
        customDialog.getWindow();
        customDialog.setCancelable(false);
        prompt = (TextView) submitDelete_view.findViewById(R.id.prompt);
        Button cancel = (Button) submitDelete_view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        Button commit = (Button) submitDelete_view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
    }

    /**
     * 输入框监听事件
     */
    private class MyTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            resultList.clear();
            resultList.addAll(siteList);
            if (s.length() > 0) {
                for (int i = 0; i < resultList.size(); i++) {
                    String str = resultList.get(i).substring(0, s.length());
                    if (!str.equalsIgnoreCase(s.toString())) {
                        resultList.remove(i);
                        i = i - 1;
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取所有监控点
     */
    private void getSiteInfo() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/WarterMonitor");

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray sites = new JSONArray(result);
                    for (int i = 0; i < sites.length(); i++) {
                        JSONObject site = sites.getJSONObject(i);
                        String name = site.getString("Name");
                        siteList.add(name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initListView();
                initDialog();
                loadingDialog.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                loadingDialog.dismiss();
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String errorResult = httpEx.getResult();
                    if (responseCode == 400) {
                        try {
                            JSONObject json = new JSONObject(errorResult);
                            String errorInfo = json.getString("Message");
                            Toast.makeText(SearchSiteActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(SearchSiteActivity.this, getString(R.string.network_error),
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
