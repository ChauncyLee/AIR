package com.liuwan.mydesign.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.liuwan.mydesign.R;
import com.liuwan.mydesign.adapter.DtuListAdapter;
import com.liuwan.mydesign.bean.DtuInfo;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.fragment.main.HomePageFragment;
import com.liuwan.mydesign.fragment.main.MeFragment;
import com.liuwan.mydesign.widget.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/10/24.
 * 主界面
 */
public class DtuListActivity  extends AppCompatActivity {

    private ListView dtuListView;
    private List<DtuInfo> mlist=new ArrayList<>();
    private DtuListAdapter dtuAdapter;
    private LinearLayout back;
    private LoadingDialog loadingDialog;
    private Integer dtuNum=0;
    private List<DtuInfo> dtuIdList=new ArrayList<DtuInfo>();
    private String to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dtu_list);

        Intent intent = getIntent();
        to = intent.getStringExtra("To");
        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.back:
                        DtuListActivity.this.finish();
                        break;
                }
            }
        });
        getDtuNum();

    }



    //获取dtu的数量
    private void getDtuNum(){
        RequestParams params = new RequestParams(MyApplication.appIp + "/MonitorId");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("dataValue",result);
                try {
                    JSONArray json = new JSONArray(result);
                    JSONObject jsonOne;

                    for(int i=0;i<json.length();i++){
                        DtuInfo dtuInfo=new DtuInfo();
                        jsonOne = json.getJSONObject(i);
                        dtuInfo.setDtu_id(jsonOne.get("MonitorId").toString());
                        dtuIdList.add(dtuInfo);
                        Log.i("dataValue",dtuIdList.toString());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initview();
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
                            Toast.makeText(DtuListActivity.this, errorInfo, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else { // 其他错误
                    Toast.makeText(DtuListActivity.this, getString(R.string.network_error),
                            Toast.LENGTH_LONG).show();
                    Log.i("dataValue","网络无法连接");

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

    private void initview() {
        dtuListView = (ListView) findViewById(R.id.dtu_list);
        dtuNum=dtuIdList.size();


        //循环添加快递item
        for(int i =0;i<dtuNum;i++) {
            DtuInfo bean = new DtuInfo();
            bean.setDtu_id("Dtu"+dtuIdList.get(i).getDtu_id());
            mlist.add(bean);
        }

        //setImageResource(drawable)
        dtuAdapter=new DtuListAdapter(this,mlist) ;
        dtuListView.setAdapter(dtuAdapter);
        dtuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(DtuListActivity.this, ParameterSelectionActivity.class);
               DtuInfo d=(DtuInfo) dtuListView.getItemAtPosition(position);
                //String dId= dList.get(0).getDtu_id();
                intent.putExtra("dId",d.getDtu_id().substring(3,4));
                Log.i("did",d.getDtu_id().substring(3,4));
                intent.putExtra("To", to);
                startActivity(intent);
            }
        });

    }
}