package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by liuwan on 2016/11/8.
 * 账户信息界面
 */
public class AccountInfoActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private RelativeLayout rlRealName, rlMobileNumber;
    private TextView tvJobNumber, tvRealName, tvMobileNumber;
    private String jobNumber, realName, mobileNumber;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountinfo);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        tvJobNumber = (TextView) findViewById(R.id.tvJobNumber);
        rlRealName = (RelativeLayout) findViewById(R.id.rlRealName);
        rlRealName.setOnClickListener(this);
        tvRealName = (TextView) findViewById(R.id.tvRealName);
        rlMobileNumber = (RelativeLayout) findViewById(R.id.rlMobileNumber);
        rlMobileNumber.setOnClickListener(this);
        tvMobileNumber = (TextView) findViewById(R.id.tvMobileNumber);

        getAccountInfo();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.rlRealName:
                realName = tvRealName.getText().toString();
                intent.setClass(AccountInfoActivity.this, ModifyAccountInfoActivity.class);
                intent.putExtra("Type", "RealName");
                intent.putExtra("RealName", realName);
                startActivityForResult(intent, 4001);
                break;

            case R.id.rlMobileNumber:
                mobileNumber = tvMobileNumber.getText().toString();
                intent.setClass(AccountInfoActivity.this, ModifyAccountInfoActivity.class);
                intent.putExtra("Type", "MobileNumber");
                intent.putExtra("MobileNumber", mobileNumber);
                startActivityForResult(intent, 4002);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4001 && resultCode == 4009) {
            realName = data.getStringExtra("RealName");
            tvRealName.setText(realName);
        } else if (requestCode == 4002 && resultCode == 4009) {
            mobileNumber = data.getStringExtra("MobileNumber");
            tvMobileNumber.setText(mobileNumber);
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
     * 获取账号信息
     */
    private void getAccountInfo() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/UserInformation");
        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        params.addBodyParameter("LoginName", sharedPreferences.getString("Username", ""));

        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject accountInfo = new JSONObject(result);
                    jobNumber = accountInfo.getString("LoginName");
                    realName = accountInfo.getString("Name");
                    mobileNumber = accountInfo.getString("MobileNumber");

                    tvJobNumber.setText(jobNumber);
                    if (realName != null) {
                        tvRealName.setText(realName);
                    } else {
                        realName = "";
                        tvRealName.setText("无");
                    }
                    if (mobileNumber != null) {
                        tvMobileNumber.setText(mobileNumber);
                    } else {
                        mobileNumber = "";
                        tvMobileNumber.setText("无");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                            Toast.makeText(AccountInfoActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(AccountInfoActivity.this, getString(R.string.network_error),
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
