package com.liuwan.mydesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.MessageList;

import java.util.List;


/**
 * Created by liuwan on 2016/12/1.
 * 消息列表适配器
 */
public class MessageListAdapter extends BaseAdapter {

    private List<MessageList> list;
    private LayoutInflater layoutInflater;
    private int flag;

    public MessageListAdapter(Context context, List<MessageList> list, int flag) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (flag == 0) {
                convertView = layoutInflater.inflate(R.layout.listview_messagelist, parent, false);
            } else {
                convertView = layoutInflater.inflate(R.layout.listview_messagelist_delete, parent, false);
                viewHolder.iconCBX = (ImageView) convertView.findViewById(R.id.iconCBX);
            }
            viewHolder.iconMsg = (ImageView) convertView.findViewById(R.id.iconMsg);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.tvType = (TextView) convertView.findViewById(R.id.tvType);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (flag == 1) {
            boolean hasSelected = list.get(position).isSelected();
            if (hasSelected) { // 选中
                viewHolder.iconCBX.setImageResource(R.drawable.checkbox2);
            } else { // 未选
                viewHolder.iconCBX.setImageResource(R.drawable.checkbox1);
            }
        }

        String hasRead = list.get(position).getIsRead();
        if ("true".equals(hasRead)) { // 已读
            viewHolder.iconMsg.setImageResource(R.drawable.message2);
        } else if ("false".equals(hasRead)) { // 未读
            viewHolder.iconMsg.setImageResource(R.drawable.message1);
        }

        viewHolder.tvTitle.setText(list.get(position).getTitle());
        viewHolder.tvType.setText(list.get(position).getType());
        viewHolder.tvTime.setText(list.get(position).getSendTime());

        return convertView;
    }

    public class ViewHolder {
        ImageView iconMsg;
        TextView tvTitle;
        TextView tvType;
        TextView tvTime;
        ImageView iconCBX;
    }

}