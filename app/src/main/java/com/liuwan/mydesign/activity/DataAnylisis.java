package com.liuwan.mydesign.activity;

import com.liuwan.mydesign.bean.NHData;
import com.liuwan.mydesign.bean.ParameterData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cerian on 2017/8/9.
 */

public class DataAnylisis {

    public  static List<NHData> encapsulateNH(String result){
        List<NHData> encapsulateNHList =new ArrayList<>();
        try{
            JSONArray array = new JSONArray(result);
            for(int i=array.length()-1;i>0;i--){
                JSONObject jsonObject=array.getJSONObject(i);
                NHData nhData=new NHData();
                float value = Float.parseFloat(jsonObject.getString("DataValue"));
                nhData.setDataValue(value);
                String time=jsonObject.getString("Time");
                nhData.setTime(time);
                encapsulateNHList.add(nhData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return encapsulateNHList;
    }

    public static List<Object> analysisToCurve2(List<NHData> parameter){
        List<String> time = new ArrayList<>();
        List<Float> value = new ArrayList<>();
        for (int i = 0; i < parameter.size(); i++) {
            time.add(parameter.get(i).getTime());
            value.add(parameter.get(i).getDataValue());
        }
        String[] xLabel = time.toArray(new String[time.size()]);
        Float[] array = value.toArray(new Float[value.size()]);
        float[] data = new float[array.length];
        // 记录最小值
        double minValue = array[0];
        // 记录最大值
        double maxValue = array[0];
        // 显示7个横坐标刻度值
        int m = parameter.size() / 6;
        int stepX = m == 0 ? 1 : m;
        for (int j = 0; j < parameter.size(); j++) {
            if (j % stepX == 0) {
                // 截取显示时和分
                xLabel[j] = xLabel[j].substring(11, 19);
            } else {
                // 存储空字符串不显示刻度
                xLabel[j] = "";
            }
            data[j] = array[j];

            if (minValue > array[j]) {
                minValue = array[j];
            }
            if (maxValue < array[j]) {
                maxValue = array[j];
            }
        }

        // 显示5个纵坐标刻度值
        String[] yLabel = new String[5];
        minValue = Math.floor(minValue);//不大于
        maxValue = Math.ceil(maxValue);//不小于
        for (int k = 0; k < yLabel.length; k++) {
            double y = (maxValue - minValue) / 4 * k + minValue;
            yLabel[k] = y + "";
        }

        List<Object> list = new ArrayList<>();
        list.add(xLabel);
        list.add(yLabel);
        list.add(data);
        return list;

    }

    public static List<Object> analysisToCurve(List<ParameterData> parameter){
        List<String> time = new ArrayList<>();
        List<Float> value = new ArrayList<>();
        for (int i = 0; i < parameter.size(); i++) {
            time.add(parameter.get(i).getTime());
            value.add(parameter.get(i).getVale());
        }
        String[] xLabel = time.toArray(new String[time.size()]);
        Float[] array = value.toArray(new Float[value.size()]);
        float[] data = new float[array.length];
        // 记录最小值
        double minValue = array[0];
        // 记录最大值
        double maxValue = array[0];
        // 显示7个横坐标刻度值
        int m = parameter.size() / 6;
        int stepX = m == 0 ? 1 : m;
        for (int j = 0; j < parameter.size(); j++) {
            if (j % stepX == 0) {
                // 截取显示时和分
                xLabel[j] = xLabel[j].substring(11, 19);
            } else {
                // 存储空字符串不显示刻度
                xLabel[j] = "";
            }
            data[j] = array[j];

            if (minValue > array[j]) {
                minValue = array[j];
            }
            if (maxValue < array[j]) {
                maxValue = array[j];
            }
        }

        // 显示5个纵坐标刻度值
        String[] yLabel = new String[5];
        minValue = Math.floor(minValue);//不大于
        maxValue = Math.ceil(maxValue);//不小于
        for (int k = 0; k < yLabel.length; k++) {
            double y = (maxValue - minValue) / 4 * k + minValue;
            yLabel[k] = y + "";
        }

        List<Object> list = new ArrayList<>();
        list.add(xLabel);
        list.add(yLabel);
        list.add(data);
        return list;

    }


}
