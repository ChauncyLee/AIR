package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by liuwan on 2016/10/28.
 * 启动界面
 */
public class GuideActivity extends Activity {

    private final static int LOGIN_ACTIVITY = 1001;
    private final static int MAIN_ACTIVITY = 1002;
    private final static int DELAYED_TIME = 2000;
    private String username, password, errorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("AutoLogin", false)) {
            username = sharedPreferences.getString("Username", "");
            password = sharedPreferences.getString("Password", "");
            validateLogin();
        } else {
            mHandler.sendEmptyMessageDelayed(LOGIN_ACTIVITY, DELAYED_TIME);
        }
    }

    /**
     * Handler:跳转至不同页面
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            switch (msg.what) {
                case LOGIN_ACTIVITY:
                    intent.setClass(GuideActivity.this, LoginActivity.class);
                    intent.putExtra("ErrorInfo", errorInfo);
                    startActivity(intent);
                    GuideActivity.this.finish();
                    break;

                case MAIN_ACTIVITY:
                    intent.setClass(GuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    GuideActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取百度云推送API key
     */
    private String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return apiKey;
    }

    /**
     * 登录验证
     */
    private void validateLogin() {
        RequestParams params = new RequestParams(MyApplication.appIp + "/Login");
        params.addBodyParameter("LoginName", username);
        params.addBodyParameter("Password", MD5.md5(password));

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                MyApplication.username = username;
                // 启动云推送
                PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY,
                        getMetaValue(GuideActivity.this, "api_key"));
                mHandler.sendEmptyMessageDelayed(MAIN_ACTIVITY, DELAYED_TIME);
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
                            errorInfo = json.getString("Message");
                            mHandler.sendEmptyMessageDelayed(LOGIN_ACTIVITY, DELAYED_TIME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(GuideActivity.this, getString(R.string.network_error),
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