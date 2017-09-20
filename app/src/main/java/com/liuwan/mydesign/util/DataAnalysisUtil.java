package com.liuwan.mydesign.util;

import android.content.Context;
import android.util.Log;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.AbnormalData;
import com.liuwan.mydesign.bean.NHData;
import com.liuwan.mydesign.bean.ParameterData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuwan on 2016/11/7.
 * 数据解析工具类
 */
public class DataAnalysisUtil {

    /**
     * mpc绘图数据
     */

    public  static List<NHData> encapsulateNH(String result){
        List<NHData> encapsulateNHList =new ArrayList<>();
        try{
            JSONArray array = new JSONArray(result);
            for(int i = 0; i < array.length(); i++){
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






    /**
     * 将污染物的正常值范围封装成数组
     */
    public static float[] encapsulateNormalRange(String result) {
        float[] normalRange = new float[]{0, 0};
        try {
            JSONObject json = new JSONObject(result);
            normalRange[0] = Float.parseFloat(json.getString("LimitValue"));
            normalRange[1] = Float.parseFloat(json.getString("ValueCeiling"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return normalRange;
    }

    /**
     * 将监测到的污染物数据封装成集合（逆序排列）
     */
    public static List<ParameterData> encapsulateParameterDataReverse(String result) {
        List<ParameterData> parameterDataList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                ParameterData parameterData = new ParameterData();
                //String name = json.getString("ParameterName");
                //parameterData.setName(name);
                float value = Float.parseFloat(json.getString("DataValue"));
                parameterData.setVale(value);
                String time = json.getString("Time");
                parameterData.setTime(time);
                parameterDataList.add(parameterData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Collections.reverse(parameterDataList);
        return parameterDataList;
    }

    /**
     * 将监测到的污染物数据封装成集合
     */
    public static List<ParameterData> encapsulateParameterData(String result) {
        List<ParameterData> parameterDataList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                ParameterData parameterData = new ParameterData();
                /*String name = json.getString("ParameterName");
                parameterData.setName(name);*/
                float value = Float.parseFloat(json.getString("DataValue"));
                parameterData.setVale(value);
                String time = json.getString("Time");
                parameterData.setTime(time);
                parameterDataList.add(parameterData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parameterDataList;
    }

    /**
     * 将数据集合解析成曲线图
     */
    public static List<Object> analysisToCurve(List<ParameterData> parameter) {
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
        minValue = Math.floor(minValue);
        maxValue = Math.ceil(maxValue);
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

    /**
     * 将数据集合分页筛选，为列表显示做准备
     */
    public static List<ParameterData> pagingForList(List<ParameterData> parameterData, int pagedNumber,
                                                    int currentPage) {
        List<ParameterData> list = new ArrayList<>();
        int start = pagedNumber * (currentPage - 1);
        int end;
        if (start + pagedNumber > parameterData.size()) {
            end = parameterData.size();
        } else {
            end = start + pagedNumber;
        }
        for (int i = start; i < end; i++) {
            list.add(parameterData.get(i));
        }
        return list;
    }

    /**
     * 将数据集合解析成两列列表
     */
    public static List<Map<String, String>> analysisTo2List(List<ParameterData> parameter) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < parameter.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("Time", parameter.get(i).getTime());
            map.put("Value", parameter.get(i).getVale() + "");
            list.add(map);
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * 将数据集合解析成四列列表
     */
    public static List<Map<String, String>> analysisTo4List(List<ParameterData> parameter,
                                                            float[] normalRange) {
        List<Map<String, String>> list = new ArrayList<>();
        for (int i = 0; i < parameter.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put("Time", parameter.get(i).getTime());
            float value = parameter.get(i).getVale();
            map.put("Value", value + "");
            map.put("NormalRange", normalRange[0] + "-" + normalRange[1]);
            float overProof = 0.0f;
            if (value < normalRange[0]) {
                overProof = value - normalRange[0];
            } else if (value > normalRange[1]) {
                overProof = value - normalRange[1];
            }
            map.put("OverProof", DisplayUtil.decimal(overProof));
            list.add(map);
        }
        Collections.reverse(list);
        return list;
    }

    /**
     * 将数据集合解析成表格
     */
    public static List<Object> analysisToTable(List<ParameterData> parameter) {
        float maxValue = parameter.get(0).getVale();
        String maxTime = parameter.get(0).getTime();
        float minValue = parameter.get(0).getVale();
        String minTime = parameter.get(0).getTime();
        float totalValue = 0;
        for (int i = 0; i < parameter.size(); i++) {
            if (parameter.get(i).getVale() > maxValue) {
                maxValue = parameter.get(i).getVale();
                maxTime = parameter.get(i).getTime();
            }
            if (parameter.get(i).getVale() < minValue) {
                minValue = parameter.get(i).getVale();
                minTime = parameter.get(i).getTime();
            }
            totalValue += parameter.get(i).getVale();
        }
        float averageValue = totalValue / parameter.size();
        List<Object> list = new ArrayList<>();
        list.add(maxValue);
        list.add(maxTime);
        list.add(minValue);
        list.add(minTime);
        list.add(DisplayUtil.decimal(averageValue));
        return list;
    }

    /**
     * 将最后一次监测到的污染物超标数据封装成集合并确定最大污染级别
     */
    public static List<AbnormalData> encapsulateAbnormalData(Context context, String result) {
        List<AbnormalData> abnormalDataList = new ArrayList<>();
        String[] str = context.getResources().getStringArray(R.array.overProofLevel);
        double[] levelArray = new double[str.length];
        for (int i = 0; i < str.length; i++) {
            levelArray[i] = Double.parseDouble(str[i]);
        }

        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject json = array.getJSONObject(i);
                String siteName = json.getString("MonitorName");
                String parameterName = null;
                int level = 0;

                double temperatureValue = "null".equals(json.getString("Temp")) ?
                        0 : Double.parseDouble(json.getString("Temp"));

                double phValue = "null".equals(json.getString("ph")) ?
                        0 : Double.parseDouble(json.getString("ph"));
                int phLevel = (int) Math.ceil(Math.abs(phValue / levelArray[1]));
                if (phLevel > level) {
                    level = phLevel;
                    parameterName = context.getResources().getString(R.string.ph);
                }

                double dissolvedOxygenValue = "null".equals(json.getString("doxygen")) ?
                        0 : Double.parseDouble(json.getString("doxygen"));
                int dissolvedOxygenLevel = (int) Math.ceil(Math.abs(dissolvedOxygenValue / levelArray[2]));
                if (dissolvedOxygenLevel > level) {
                    level = dissolvedOxygenLevel;
                    parameterName = context.getResources().getString(R.string.dissolved_oxygen);
                }

                double turbidityValue = "null".equals(json.getString("Turbidity")) ?
                        0 : Double.parseDouble(json.getString("Turbidity"));
                int turbidityLevel = (int) Math.ceil(Math.abs(turbidityValue / levelArray[3]));
                if (turbidityLevel > level) {
                    level = turbidityLevel;
                    parameterName = context.getResources().getString(R.string.turbidity);
                }

                double conductivityValue = "null".equals(json.getString("Conductivity")) ?
                        0 : Double.parseDouble(json.getString("Conductivity"));
                int conductivityLevel = (int) Math.ceil(Math.abs(conductivityValue / levelArray[4]));
                if (conductivityLevel > level) {
                    level = conductivityLevel;
                    parameterName = context.getResources().getString(R.string.conductivity);
                }

                double blueGreenAlgaeValue = "null".equals(json.getString("Bluegreenalgae")) ?
                        0 : Double.parseDouble(json.getString("Bluegreenalgae"));
                int blueGreenAlgaeLevel = (int) Math.ceil(Math.abs(blueGreenAlgaeValue / levelArray[5]));
                if (blueGreenAlgaeLevel > level) {
                    level = blueGreenAlgaeLevel;
                    parameterName = context.getResources().getString(R.string.blue_green_algae);
                }

                double chlorophyllValue = "null".equals(json.getString("Chlorophyll")) ?
                        0 : Double.parseDouble(json.getString("Chlorophyll"));
                int chlorophyllLevel = (int) Math.ceil(Math.abs(chlorophyllValue / levelArray[6]));
                if (chlorophyllLevel > level) {
                    level = chlorophyllLevel;
                    parameterName = context.getResources().getString(R.string.chlorophyll);
                }

                double ammoniaNitrogenValue = "null".equals(json.getString("Ammonianitrogen")) ?
                        0 : Double.parseDouble(json.getString("Ammonianitrogen"));
                int ammoniaNitrogenLevel = (int) Math.ceil(Math.abs(ammoniaNitrogenValue / levelArray[7]));
                if (ammoniaNitrogenLevel > level) {
                    level = ammoniaNitrogenLevel;
                    parameterName = context.getResources().getString(R.string.ammonia_nitrogen);
                }

                double waterFlowRate = "null".equals(json.getString("WarterSpeed")) ?
                        0 : Double.parseDouble(json.getString("WarterSpeed"));

                level = level > 5 ? 5 : level;

                AbnormalData abnormalData = new AbnormalData();
                abnormalData.setSiteName(siteName);
                abnormalData.setTemperatureValue(temperatureValue);
                abnormalData.setPhValue(phValue);
                abnormalData.setDissolvedOxygenValue(dissolvedOxygenValue);
                abnormalData.setTurbidityValue(turbidityValue);
                abnormalData.setConductivityValue(conductivityValue);
                abnormalData.setBlueGreenAlgaeValue(blueGreenAlgaeValue);
                abnormalData.setChlorophyllValue(chlorophyllValue);
                abnormalData.setAmmoniaNitrogenValue(ammoniaNitrogenValue);
                abnormalData.setWaterFlowRate(waterFlowRate);
                abnormalData.setParameterName(parameterName);
                abnormalData.setLevel(level);

                // 排除无异常的站点数据
                if (level != 0) {
                    abnormalDataList.add(abnormalData);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return abnormalDataList;
    }

}