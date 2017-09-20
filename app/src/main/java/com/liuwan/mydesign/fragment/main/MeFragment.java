package com.liuwan.mydesign.fragment.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.activity.AccountInfoActivity;
import com.liuwan.mydesign.activity.LoginActivity;
import com.liuwan.mydesign.activity.MessageListActivity;
import com.liuwan.mydesign.activity.ModifyPasswordActivity;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by liuwan on 2016/9/24.
 * 我界面
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout message, accountInfo, modifyPassword, logout;
    private TextView tvSuperscript;
    private CustomDialog customDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        accountInfo = (LinearLayout) getActivity().findViewById(R.id.accountInfo);
        accountInfo.setOnClickListener(this);
        modifyPassword = (LinearLayout) getActivity().findViewById(R.id.modifyPassword);
        modifyPassword.setOnClickListener(this);
        logout = (LinearLayout) getActivity().findViewById(R.id.logout);
        logout.setOnClickListener(this);


//        getMessageNumber();
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_me, container, false);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {

            case R.id.accountInfo:
                intent.setClass(getActivity(), AccountInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.modifyPassword:
                intent.setClass(getActivity(), ModifyPasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                customDialog.show();
                break;

            case R.id.cancel:
                customDialog.dismiss();
                break;

            case R.id.commit:
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyDesign",
                        Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Password", "");
                editor.putBoolean("AutoLogin", false);
                editor.apply();
                customDialog.dismiss();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    /**
     * 获得返回结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 5001 && resultCode == 5009) {
//            getMessageNumber();
        }
    }

    /**
     * 初始化Dialog
     */
    @SuppressLint("InflateParams")
    private void initDialog() {
        View submitDelete_view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_confirm, null);
        customDialog = new CustomDialog(getActivity(), 0.83, 0.22, submitDelete_view, R.style.customDialogStyle);
        customDialog.getWindow();
        customDialog.setCancelable(false);
        TextView prompt = (TextView) submitDelete_view.findViewById(R.id.prompt);
        prompt.setText("退出当前账户？");
        Button cancel = (Button) submitDelete_view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        Button commit = (Button) submitDelete_view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
    }

    /**
     * 获取未读消息数
     */
//    private void getMessageNumber() {
//        RequestParams params = new RequestParams(MyApplication.appIp + "/Messages/New/Count");
//
//        x.http().get(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                if ("null".equals(result) || "0".equals(result)) {
//                    tvSuperscript.setVisibility(View.GONE);
//                } else {
//                    tvSuperscript.setVisibility(View.VISIBLE);
//                    tvSuperscript.setText(result);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean b) {
//                if (ex instanceof HttpException) { // 网络错误
//                    HttpException httpEx = (HttpException) ex;
//                    int responseCode = httpEx.getCode();
//                    String errorResult = httpEx.getResult();
//                    if (responseCode == 400) {
//                        try {
//                            JSONObject json = new JSONObject(errorResult);
//                            String errorInfo = json.getString("Message");
//                            Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_LONG).show();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else { // 其他错误
//                    Toast.makeText(getActivity(), getString(R.string.network_error),
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(CancelledException e) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//
//        });
//
//    }

}
