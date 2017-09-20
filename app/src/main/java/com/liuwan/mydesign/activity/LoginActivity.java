package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.MD5;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by liuwan on 2016/10/24.
 * 登录界面
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText edUsername, edPassword;
    private LinearLayout checkBox;
    private CheckBox cbRememberPassword;
    private Button btnLogin;
    private LoadingDialog loadingDialog;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edUsername = (EditText) findViewById(R.id.edUsername);
        edPassword = (EditText) findViewById(R.id.edPassword);
        checkBox = (LinearLayout) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);
        cbRememberPassword = (CheckBox) findViewById(R.id.cbRememberPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Intent intent = getIntent();
        String errorInfo = intent.getStringExtra("ErrorInfo");
        if (!"".equals(errorInfo) && errorInfo != null) {
            Toast.makeText(this, errorInfo, Toast.LENGTH_LONG).show();
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        edUsername.setText(sharedPreferences.getString("Username", ""));
        edPassword.setText(sharedPreferences.getString("Password", ""));
        cbRememberPassword.setChecked(sharedPreferences.getBoolean("AutoLogin", false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkBox:
                if (cbRememberPassword.isChecked()) {
                    cbRememberPassword.setChecked(false);
                } else {
                    cbRememberPassword.setChecked(true);
                }
                break;

            case R.id.btnLogin:
                username = edUsername.getText().toString();
                password = edPassword.getText().toString();
                if (username.trim().equals("")) {
                    Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (password.trim().equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    validateLogin();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

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
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Login");
        params.addBodyParameter("LoginName", username);
        params.addBodyParameter("Password", password);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                MyApplication.username = username;


                SharedPreferences sharedPreferences = getSharedPreferences("MyDesign",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Username", username);
                editor.putString("Password", password);
                if (cbRememberPassword.isChecked()) {
                    editor.putBoolean("AutoLogin", true);
                } else {
                    editor.putBoolean("AutoLogin", false);
                }
                editor.apply();

                loadingDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                Log.i("success","ssssss");
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
                            Toast.makeText(LoginActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(LoginActivity.this, getString(R.string.network_error),
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
