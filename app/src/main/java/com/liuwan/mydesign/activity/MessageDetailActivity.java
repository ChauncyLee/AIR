package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.CustomDialog;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by liuwan on 2016/12/2.
 * 消息详情界面
 */
public class MessageDetailActivity extends Activity implements View.OnClickListener {

    private LinearLayout back;
    private TextView delete;
    private TextView tvTitle, tvType, tvTime, tvSite, tvParameter, tvValue, tvOverProof;
    private LoadingDialog loadingDialog;
    private CustomDialog customDialog;
    private String messageId;
    private String siteName, type, sendTime, parameter, value, overProof;
    private boolean isRead = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagedetail);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvType = (TextView) findViewById(R.id.tvType);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvSite = (TextView) findViewById(R.id.tvSite);
        tvParameter = (TextView) findViewById(R.id.tvParameter);
        tvValue = (TextView) findViewById(R.id.tvValue);
        tvOverProof = (TextView) findViewById(R.id.tvOverProof);

        Intent intent = getIntent();
        messageId = intent.getStringExtra("MessageId");

        if (messageId == null) {
            messageId = MyApplication.messageId;
        }

        getMessageDetail();
        initDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (isRead) {
                    Intent intent = new Intent(MessageDetailActivity.this, MessageListActivity.class);
                    setResult(6008, intent);
                }
                this.finish();
                break;

            case R.id.delete:
                customDialog.show();
                break;

            case R.id.cancel:
                customDialog.dismiss();
                break;

            case R.id.commit:
                customDialog.dismiss();
                deleteMessage();
                break;
        }
    }

    /**
     * 监听Back键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isRead) {
                Intent intent = new Intent(MessageDetailActivity.this, MessageListActivity.class);
                setResult(6008, intent);
            }
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化Dialog
     */
    @SuppressLint("InflateParams")
    private void initDialog() {
        View submitDelete_view = LayoutInflater.from(MessageDetailActivity.this).
                inflate(R.layout.dialog_confirm, null);
        customDialog = new CustomDialog(MessageDetailActivity.this, 0.83, 0.22, submitDelete_view,
                R.style.customDialogStyle);
        customDialog.getWindow();
        customDialog.setCancelable(false);
        TextView prompt = (TextView) submitDelete_view.findViewById(R.id.prompt);
        prompt.setText("删除当前消息？");
        Button cancel = (Button) submitDelete_view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        Button commit = (Button) submitDelete_view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
    }

    /**
     * 读取消息
     */
    @SuppressLint("SetTextI18n")
    private void getMessageDetail() {
        loadingDialog = new LoadingDialog(MessageDetailActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Message/Read/" + messageId);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    siteName = json.getString("Title");
                    type = json.getString("TypeName");
                    sendTime = json.getString("SendTime");
                    parameter = json.getString("Parameter");
                    value = json.getString("Content");
                    overProof = json.getString("Over");

                    tvTitle.setText(siteName);
                    tvType.setText(type);
                    tvTime.setText(sendTime);
                    tvSite.setText(siteName);
                    tvParameter.setText(parameter);
                    tvValue.setText(value);
                    tvOverProof.setText(overProof);
                    isRead = true;
                } catch (JSONException e) {
                    e.printStackTrace();
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
                            Toast.makeText(MessageDetailActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MessageDetailActivity.this, getString(R.string.network_error),
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
     * 删除消息
     */
    private void deleteMessage() {
        loadingDialog = new LoadingDialog(MessageDetailActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Message/Delete/" + messageId);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Intent intent = new Intent(MessageDetailActivity.this, MessageListActivity.class);
                setResult(6009, intent);
                MessageDetailActivity.this.finish();

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
                            Toast.makeText(MessageDetailActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MessageDetailActivity.this, getString(R.string.network_error),
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