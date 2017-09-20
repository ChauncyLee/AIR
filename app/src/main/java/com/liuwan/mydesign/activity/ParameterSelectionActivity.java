package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.adapter.GridViewAdapter;

/**
 * Created by liuwan on 2016/11/19.
 * 参数选择界面
 */
public class ParameterSelectionActivity extends Activity implements View.OnClickListener {

    private LinearLayout back, gridViewParent;
    private GridView gridView;
    private String to;
    private String dId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameterselection);

        Intent intent = getIntent();
        to = intent.getStringExtra("To");
        dId=intent.getStringExtra("dId");
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        gridViewParent = (LinearLayout) findViewById(R.id.gridViewParent);
        gridView = (GridView) findViewById(R.id.gridView);

        initGridView();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化GridView
     */
    public void initGridView() {
        // 图标数组
        TypedArray typedArray = getResources().obtainTypedArray(R.array.parameterSelection_GridView_Image);
        int len = typedArray.length();
        int[] gridViewImage = new int[len];
        for (int i = 0; i < len; i++) {
            gridViewImage[i] = typedArray.getResourceId(i, 0);
        }
        typedArray.recycle();
        // 文字数组
        String[] gridViewText = getResources().getStringArray(R.array.parameterSelection_GridView_Text);

        gridView.setAdapter(new GridViewAdapter(this, gridViewParent, gridViewImage, gridViewText, 3));
        // 注册监听事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent();
                if (to.equals(getResources().getString(R.string.real_time_monitoring))) {
                    intent.setClass(ParameterSelectionActivity.this, RealTimeMonitoringActivity.class);
                } else if (to.equals(getResources().getString(R.string.historical_data_query))) {
                    intent.setClass(ParameterSelectionActivity.this, HistoricalDataQueryActivity.class);
                }/* else if (to.equals(getResources().getString(R.string.abnormal_data_query))) {
                    intent.setClass(ParameterSelectionActivity.this, AbnormalDataQueryActivity.class);
                }*/

                switch (position) {
                    case 0:
                        if (to.equals(getResources().getString(R.string.abnormal_data_query))) {
                            Toast.makeText(ParameterSelectionActivity.this,
                                    getResources().getString(R.string.unavailable), Toast.LENGTH_LONG).show();
                        } else {
                            intent.putExtra("Parameter", getResources().getString(R.string.temperature)+"℃");
                            intent.putExtra("dId",dId);
                            intent.putExtra("route","Temperature");
                            startActivity(intent);
                        }
                        break;

                    case 1:
                        intent.putExtra("Parameter", getResources().getString(R.string.humidity)+"%");
                        intent.putExtra("route","Humidity");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;

                    case 2:
                        intent.putExtra("Parameter", getResources().getString(R.string.wind_speed)+"m/s");
                        intent.putExtra("route","WindSpeed");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;

                    case 3:
                        intent.putExtra("Parameter", getResources().getString(R.string.atmos)+"hpa");
                        intent.putExtra("route","Atmos");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.putExtra("Parameter",getResources().getString(R.string.NH3)+"mg/m3");
                        intent.putExtra("route","NH3");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.putExtra("Parameter",getResources().getString(R.string.H2S)+"mg/m3");
                        intent.putExtra("route","H2S");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.putExtra("Parameter",getResources().getString(R.string.CH4)+"mg/m3");
                        intent.putExtra("route","CH4");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;
                    case 7:
                        intent.putExtra("Parameter",getResources().getString(R.string.CO2)+"mg/m3");
                        intent.putExtra("route","CO2");
                        intent.putExtra("dId",dId);
                        startActivity(intent);
                        break;

                }
            }
        });
    }
}
