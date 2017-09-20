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

/**
 * Created by liuwan on 2016/11/8.
 * 修改真实姓名
 */
public class ModifyRealNameFragment extends Fragment implements View.OnClickListener {

    private EditText edRealName;
    private Button submit;
    private String realName;
    private LoadingDialog loadingDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        edRealName = (EditText) getActivity().findViewById(R.id.edRealName);
        submit = (Button) getActivity().findViewById(R.id.submit);
        submit.setOnClickListener(this);

        realName = getArguments().getString("RealName");
        edRealName.setText(realName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accountinfo_modifyrealname, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                realName = edRealName.getText().toString();
                if ("".equals(realName)) {
                    Toast.makeText(getActivity(), "请输入真实姓名", Toast.LENGTH_LONG).show();
                } else {
                    realName = edRealName.getText().toString();
                    modifyRealName();
                }
                break;
        }
    }

    /**
     * 修改姓名
     */
    private void modifyRealName() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/ModifyName");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyDesign",
                Activity.MODE_PRIVATE);
        params.addBodyParameter("LoginName", sharedPreferences.getString("Username", ""));
        params.addBodyParameter("Name", realName);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                loadingDialog.dismiss();
                Toast.makeText(getActivity(), "姓名修改成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra("RealName", realName);
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
