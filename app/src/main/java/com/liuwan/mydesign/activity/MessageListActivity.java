package com.liuwan.mydesign.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.adapter.MessageListAdapter;
import com.liuwan.mydesign.bean.MessageList;
import com.liuwan.mydesign.bean.MyApplication;
import com.liuwan.mydesign.widget.CustomDialog;
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
 * Created by liuwan on 2016/12/1.
 * 消息列表界面
 */
public class MessageListActivity extends Activity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout back;
    private TextView delete;
    private SwipeRefreshLayout swipeRefresh;
    private ListView listView;
    private LinearLayout bottom;
    private Button submitDelete;
    private LoadingDialog loadingDialog;
    private CustomDialog customDialog;
    private List<MessageList> messageListList;
    private MessageListAdapter adapter;
    private int flag = 0, readPosition;
    // 存储滑动位置状态
    private Parcelable state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messagelist);

        back = (LinearLayout) findViewById(R.id.back);
        back.setOnClickListener(this);
        delete = (TextView) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.list_message);
        bottom = (LinearLayout) findViewById(R.id.bottom);
        submitDelete = (Button) findViewById(R.id.submitDelete);
        submitDelete.setOnClickListener(this);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(R.color.application_green);
        swipeRefresh.setOnRefreshListener(this);

        getMessageList();
        initDialog();
        initListView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent intent = new Intent(MessageListActivity.this, MainActivity.class);
                setResult(5009, intent);
                this.finish();
                break;

            case R.id.delete:
                if (flag == 0) {
                    flag = 1;
                    delete.setText("取消");
                    bottom.setVisibility(View.VISIBLE);
                    adapter = new MessageListAdapter(this, messageListList, flag);
                    listView.setAdapter(adapter);
                } else if (flag == 1) {
                    for (int i = 0; i < messageListList.size(); i++) {
                        if (messageListList.get(i).isSelected()) {
                            messageListList.get(i).setSelected(false);
                        }
                    }
                    flag = 0;
                    delete.setText("删除");
                    bottom.setVisibility(View.GONE);
                    adapter = new MessageListAdapter(this, messageListList, flag);
                    listView.setAdapter(adapter);
                }
                // 恢复位置状态
                listView.onRestoreInstanceState(state);
                break;

            case R.id.submitDelete:
                customDialog.show();
                break;

            case R.id.cancel:
                customDialog.dismiss();
                break;

            case R.id.commit:
                customDialog.dismiss();
                deleteMessageList();
                break;
        }
    }

    /**
     * 获得返回结果
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 6001 && resultCode == 6008) { // 消息已读
            // 第一个可见Item在ListView中的位置
            int first = listView.getFirstVisiblePosition();
            // getChildAt ( int position ) 方法中position指的是当前可见区域的第几个元素
            LinearLayout layout = (LinearLayout) listView.getChildAt(readPosition - first);
            ImageView iconMsg = (ImageView) layout.findViewById(R.id.iconMsg);
            iconMsg.setImageResource(R.drawable.message2);
            messageListList.get(readPosition).setIsRead("true");
        } else if (requestCode == 6001 && resultCode == 6009) { // 消息被删除
            messageListList.remove(readPosition);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * SwipeRefreshLayout刷新事件
     */
    @Override
    public void onRefresh() {
        getMessageList();
    }

    /**
     * 监听Back键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MessageListActivity.this, MainActivity.class);
            setResult(5009, intent);
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
        View submitDelete_view = LayoutInflater.from(MessageListActivity.this).
                inflate(R.layout.dialog_confirm, null);
        customDialog = new CustomDialog(MessageListActivity.this, 0.83, 0.22, submitDelete_view,
                R.style.customDialogStyle);
        customDialog.getWindow();
        customDialog.setCancelable(false);
        TextView prompt = (TextView) submitDelete_view.findViewById(R.id.prompt);
        prompt.setText("删除所选消息？");
        Button cancel = (Button) submitDelete_view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        Button commit = (Button) submitDelete_view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        // ListView点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag == 0) {
                    Intent intent = new Intent(MessageListActivity.this, MessageDetailActivity.class);
                    intent.putExtra("MessageId", messageListList.get(position).getId());
                    startActivityForResult(intent, 6001);
                    readPosition = position;
                } else if (flag == 1) {
                    // 第一个可见View的位置
                    int first = listView.getFirstVisiblePosition();
                    // getChildAt(int position)方法中的position指的是当前可见区域的第几个元素
                    LinearLayout layout = (LinearLayout) listView.getChildAt(position - first);
                    ImageView iconCBX = (ImageView) layout.findViewById(R.id.iconCBX);
                    if (messageListList.get(position).isSelected()) { // 取消选中
                        messageListList.get(position).setSelected(false);
                        iconCBX.setImageResource(R.drawable.checkbox1);
                    } else { // 选中
                        messageListList.get(position).setSelected(true);
                        iconCBX.setImageResource(R.drawable.checkbox2);
                    }
                    adapter = new MessageListAdapter(MessageListActivity.this, messageListList, flag);
                    listView.setAdapter(adapter);
                    listView.onRestoreInstanceState(state);
                }
            }

        });

        // 记录ListView滑动位置
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                state = listView.onSaveInstanceState();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
            }

        });
    }

    /**
     * 获取所有消息
     */
    private void getMessageList() {
        loadingDialog = new LoadingDialog(MessageListActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Messages");

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray messages = new JSONArray(result);
                    messageListList = new ArrayList<>();
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject message = messages.getJSONObject(i);
                        MessageList messageList = new MessageList();
                        // 消息id
                        messageList.setId(message.getString("Id"));
                        // 消息标题
                        messageList.setTitle(message.getString("Title"));
                        // 消息类型
                        messageList.setType(message.getString("TypeName"));
                        // 发送时间
                        messageList.setSendTime(message.getString("SenderTime"));
                        // 是否已阅读
                        messageList.setIsRead(message.getString("IsRead"));
                        messageListList.add(messageList);
                    }
                    adapter = new MessageListAdapter(MessageListActivity.this, messageListList, flag);
                    listView.setAdapter(adapter);
                    state = listView.onSaveInstanceState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeRefresh.setRefreshing(false);
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onError(Throwable ex, boolean b) {
                swipeRefresh.setRefreshing(false);
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
                            Toast.makeText(MessageListActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MessageListActivity.this, getString(R.string.network_error),
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
     * 批量删除消息
     */
    private void deleteMessageList() {
        loadingDialog = new LoadingDialog(MessageListActivity.this);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        RequestParams params = new RequestParams(MyApplication.appIp + "/Messages/Delete");
        for (int i = 0; i < messageListList.size(); i++) {
            if (messageListList.get(i).isSelected()) {
                params.addBodyParameter("Messages", messageListList.get(i).getId());
            }
        }

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                for (int i = 0; i < messageListList.size(); i++) {
                    if (messageListList.get(i).isSelected()) {
                        messageListList.remove(i);
                        i = i - 1;
                    }
                }
                adapter.notifyDataSetChanged();

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
                            Toast.makeText(MessageListActivity.this, errorInfo,
                                    Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else { // 其他错误
                    Toast.makeText(MessageListActivity.this, getString(R.string.network_error),
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