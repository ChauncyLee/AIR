package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.widget.timeselector.TimeSelector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liuwan on 2016/10/26.
 * 条件筛选界面
 */
public class ConditionFilterActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private RelativeLayout selectSite, selectStartTime, selectEndTime;
    private TextView tvSite, tvStartTime, tvEndTime;
    private RadioGroup radioGroup;
    private Button commit;
    private TimeSelector timeSelector;
    private String site, startTime, endTime;
    private SimpleDateFormat sdf;
    private Calendar calendar = Calendar.getInstance();
    private Date date;
    private String from;
    private String dId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditionfilter);

        Intent intent = getIntent();
        from = intent.getStringExtra("From");

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        commit = (Button) findViewById(R.id.commit);
        commit.setOnClickListener(this);

        selectSite = (RelativeLayout) findViewById(R.id.selectSite);
        selectSite.setOnClickListener(this);
        tvSite = (TextView) findViewById(R.id.tvSite);

        selectStartTime = (RelativeLayout) findViewById(R.id.selectStartTime);
        selectStartTime.setOnClickListener(this);
        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        selectEndTime = (RelativeLayout) findViewById(R.id.selectEndTime);
        selectEndTime.setOnClickListener(this);
        tvEndTime = (TextView) findViewById(R.id.tvEndTime);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    String result = radioButton.getText().toString();
                    if ("今天".equals(result)) {
                        calendar.setTime(new Date());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        date = calendar.getTime();
                        startTime = sdf.format(date);

                        endTime = sdf.format(new Date());

                        tvStartTime.setText(startTime);
                        tvEndTime.setText(endTime);

                    } else if ("昨天".equals(result)) {
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        date = calendar.getTime();
                        startTime = sdf.format(date);

                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.SECOND, -1);
                        date = calendar.getTime();
                        endTime = sdf.format(date);

                        tvStartTime.setText(startTime);
                        tvEndTime.setText(endTime);

                    } else if ("一周".equals(result)) {
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_MONTH, -7);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        date = calendar.getTime();
                        startTime = sdf.format(date);

                        endTime = sdf.format(new Date());

                        tvStartTime.setText(startTime);
                        tvEndTime.setText(endTime);
                    }
                }
            }
        });


        dId=intent.getStringExtra("dId");
        site=dId;
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.selectSite:
                Intent intent = new Intent(ConditionFilterActivity.this, SearchSiteActivity.class);
                intent.putExtra("From", "ConditionFilter");
                startActivityForResult(intent, 1002);
                break;

            case R.id.selectStartTime:
                timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        startTime = time;
                        tvStartTime.setText(startTime);
                        radioGroup.clearCheck();
                    }
                }, "2010-01-01 00:00", sdf.format(new Date()));
                timeSelector.showSpecificTime(true);
                timeSelector.setIsLoop(true);
                timeSelector.show(tvStartTime.getText().toString());
                break;

            case R.id.selectEndTime:
                timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        endTime = time;
                        tvEndTime.setText(endTime);
                        radioGroup.clearCheck();
                    }
                }, "2010-01-01 00:00", sdf.format(new Date()));
                timeSelector.showSpecificTime(true);
                timeSelector.setIsLoop(true);
                timeSelector.show(tvEndTime.getText().toString());
                break;

            case R.id.commit:
                try {
                    if (sdf.parse(startTime).before(sdf.parse(endTime))) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Intent backIntent = new Intent();
                        if ("HistoricalDataQuery".equals(from)) {
                            editor.putString("SelectedSite", site);
                            editor.putString("SelectedStartTime", startTime);
                            editor.putString("SelectedEndTime", endTime);
                            backIntent.setClass(ConditionFilterActivity.this, HistoricalDataQueryActivity.class);
                            backIntent.putExtra("SelectedSite", site);
                            backIntent.putExtra("SelectedStartTime", startTime);
                            backIntent.putExtra("SelectedEndTime", endTime);
                            setResult(2009, backIntent);
                        } else if ("ExceptionDataQuery".equals(from)) {
                            editor.putString("SelectedSiteEx", site);
                            editor.putString("SelectedStartTimeEx", startTime);
                            editor.putString("SelectedEndTimeEx", endTime);
                            backIntent.setClass(ConditionFilterActivity.this, AbnormalDataQueryActivity.class);
                            backIntent.putExtra("SelectedSiteEx", site);
                            backIntent.putExtra("SelectedStartTimeEx", startTime);
                            backIntent.putExtra("SelectedEndTimeEx", endTime);
                            setResult(3009, backIntent);
                        }
                        editor.apply();
                        this.finish();
                    } else {
                        Toast.makeText(this, "结束时间需大于起始时间", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == 1009) {
            site = data.getStringExtra("SelectedSite");
            tvSite.setText(site);
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
     * 初始化默认选中的站点和时间
     */
    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        if ("HistoricalDataQuery".equals(from)) {
            site = sharedPreferences.getString("SelectedSite", getString(R.string.default_site));
            startTime = sharedPreferences.getString("SelectedStartTime", sdf.format(new Date()));
            endTime = sharedPreferences.getString("SelectedEndTime", sdf.format(new Date()));
        } else if ("ExceptionDataQuery".equals(from)) {
            site = sharedPreferences.getString("SelectedSiteEx", getString(R.string.default_site));
            startTime = sharedPreferences.getString("SelectedStartTimeEx", sdf.format(new Date()));
            endTime = sharedPreferences.getString("SelectedEndTimeEx", sdf.format(new Date()));
        }
        tvSite.setText(site);
        tvStartTime.setText(startTime);
        tvEndTime.setText(endTime);
    }

}
