package com.liuwan.mydesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuwan.mydesign.R;

/**
 * Created by liuwan on 2016/10/24.
 * 首页功能模块适配器
 */
public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private LinearLayout gridViewLayout;
    private int[] image;
    private String[] text;
    private int row;

    public GridViewAdapter(Context context, LinearLayout gridViewLayout, int[] image, String[] text, int row) {
        this.context = context;
        this.gridViewLayout = gridViewLayout;
        this.image = image;
        this.text = text;
        this.row = row;
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_homepage, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    gridViewLayout.getHeight() / row));
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    gridViewLayout.getHeight() / row));
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageResource(image[position]);
        viewHolder.textView.setText(text[position]);
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

}
