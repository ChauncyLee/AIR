package com.liuwan.mydesign.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
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
 * Created by liuwan on 2016/11/8.
 * 修改密码界面
 */
public class ModifyPasswordActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private EditText edOldPassword, edNewPassword, edConfirmPassword;
    private Button submit;
    private String oldPassword, newPassword, confirmPassword;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypassword);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        edOldPassword = (EditText) findViewById(R.id.edOldPassword);
        edNewPassword = (EditText) findViewById(R.id.edNewPassword);
        edConfirmPassword = (EditText) findViewById(R.id.edConfirmPassword);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            case R.id.submit:
                oldPassword = edOldPassword.getText().toString();
                newPassword = edNewPassword.getText().toString();
                confirmPassword = edConfirmPassword.getText().toString();
                if ("".equals(oldPassword)) {
                    Toast.makeText(this, "请输入旧密码", Toast.LENGTH_LONG).show();
                } else if ("".equals(newPassword)) {
                    Toast.makeText(this, "请输入新密码", Toast.LENGTH_LONG).show();
                } else if (newPassword.length() < 6) {
                    Toast.makeText(this, "新密码长度过短", Toast.LENGTH_LONG).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
                } else {
                    oldPassword = edOldPassword.getText().toString();
                    newPassword = edNewPassword.getText().toString();
                    modifyPassword();
                }
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
     * 修改密码
     */
    private void modifyPassword() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/ModifyPassword");
        SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
        params.addBodyParameter("LoginName", sharedPreferences.getString("Username", ""));
        params.addBodyParameter("OldPassword", oldPassword);
        params.addBodyParameter("Password", newPassword);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyDesign", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Password", "");
                editor.putBoolean("AutoLogin", false);
                editor.apply();

                loadingDialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(ModifyPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.instance.finish();
                Toast.makeText(ModifyPasswordActivity.this, "密码修改成功，请重新登录", Toast.LENGTH_LONG).show();
                ModifyPasswordActivity.this.finish();
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
                            Toast.makeText(ModifyPasswordActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(ModifyPasswordActivity.this, getString(R.string.network_error),
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
