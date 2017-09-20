package com.liuwan.mydesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.DtuInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Li on 2017/8/1.
 */

public class DtuListAdapter extends BaseAdapter {
    private Context context;
    private List<DtuInfo> mylist =new ArrayList<>();
    private LayoutInflater inflater;
    private DtuInfo dtuInfo;

    public DtuListAdapter(Context context,List<DtuInfo> mylist) {
        this.context=context;
        this.mylist=mylist;
        inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public Object getItem(int position) {
        return mylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vHolder=null;
        if(convertView==null){
            vHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.activity_dtu_item,null);
            vHolder.dtu_id= (TextView) convertView.findViewById(R.id.dtu_id);
            convertView.setTag(vHolder);
        }
        else{
            vHolder = (ViewHolder) convertView.getTag();
        }

        //设置数据
        dtuInfo= (DtuInfo) getItem(position);
        vHolder.dtu_id.setText(dtuInfo.getDtu_id());
        return convertView;

    }
    /* 优化*/
    class ViewHolder {
        private TextView dtu_id;
    }
}
