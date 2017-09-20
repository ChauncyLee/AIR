package com.liuwan.mydesign.fragment.modifyaccountinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by liuwan on 2016/11/8.
 * 修改手机号
 */
public class ModifyMobileNumberFragment extends Fragment implements View.OnClickListener {

    private EditText edMobileNumber;
    private Button submit;
    private String mobileNumber;
    private LoadingDialog loadingDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        edMobileNumber = (EditText) getActivity().findViewById(R.id.edMobileNumber);
        submit = (Button) getActivity().findViewById(R.id.submit);
        submit.setOnClickListener(this);

        mobileNumber = getArguments().getString("MobileNumber");
        edMobileNumber.setText(mobileNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accountinfo_modifymobilenumber, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                mobileNumber = edMobileNumber.getText().toString();
                if ("".equals(mobileNumber)) {
                    Toast.makeText(getActivity(), "请输入手机号", Toast.LENGTH_LONG).show();
                } else if (!isChinaPhoneLegal(mobileNumber)) {
                    Toast.makeText(getActivity(), "请输入正确格式的手机号", Toast.LENGTH_LONG).show();
                } else {
                    mobileNumber = edMobileNumber.getText().toString();
                    modifyMobileNumber();
                }
                break;
        }
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    private boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 修改手机号
     */
    private void modifyMobileNumber() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/ModifyMobileNumber");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyDesign",
                Activity.MODE_PRIVATE);
        params.addBodyParameter("LoginName", sharedPreferences.getString("Username", ""));
        params.addBodyParameter("MobileNumber", mobileNumber);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "手机号修改成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("MobileNumber", mobileNumber);
                getActivity().setResult(4009, intent);
                getActivity().finish();
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
                            Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(getActivity(), getString(R.string.network_error),
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
