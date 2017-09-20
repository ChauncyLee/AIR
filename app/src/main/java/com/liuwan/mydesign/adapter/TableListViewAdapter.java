package com.liuwan.mydesign.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liuwan.mydesign.R;

import java.util.List;
import java.util.Map;

/**
 * Created by liuwan on 2016/11/4.
 * 四列列表适配器
 */
public class TableListViewAdapter extends BaseAdapter {

    private List<Map<String, String>> list;
    private LayoutInflater layoutInflater;
    private int column;

    public TableListViewAdapter(Context context, List<Map<String, String>> list, int column) {
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.column = column;
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
            if (column == 4) {
                convertView = layoutInflater.inflate(R.layout.listview_table_col_4, parent, false);
            } else {
                convertView = layoutInflater.inflate(R.layout.listview_table_col_2, parent, false);
            }

            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.value = (TextView) convertView.findViewById(R.id.value);
            if (column == 4) {
                viewHolder.normalRange = (TextView) convertView.findViewById(R.id.range);
                viewHolder.overProof = (TextView) convertView.findViewById(R.id.overproof);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Map<String, String> map = list.get(position);
        viewHolder.time.setText(map.get("Time"));
        viewHolder.value.setText(map.get("Value"));
        if (column == 4) {
            viewHolder.normalRange.setText(map.get("NormalRange"));
            viewHolder.overProof.setText(map.get("OverProof"));
        }
        return convertView;
    }

    public class ViewHolder {
        TextView time;
        TextView value;
        TextView normalRange;
        TextView overProof;
    }

}