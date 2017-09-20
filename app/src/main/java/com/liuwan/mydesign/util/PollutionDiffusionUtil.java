package com.liuwan.mydesign.util;

import android.content.Context;

import com.liuwan.mydesign.R;
import com.liuwan.mydesign.bean.AbnormalData;
import com.liuwan.mydesign.bean.ParameterInfo;
import com.liuwan.mydesign.bean.RiverNodeInfo;
import com.liuwan.mydesign.bean.SiteInfo;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuwan on 2016/11/18.
 * 污染超标级别计算工具类
 */
public class PollutionDiffusionUtil {

    /**
     * 分析站点在河道中的位置，即污染扩散的起始点
     */
    public static List<Integer> analysisStartPosition(List<AbnormalData> abnormalDataList,
                                                      List<SiteInfo> siteList,
                                                      List<RiverNodeInfo> riverNodeList) {
        List<Integer> nodePositionList = new ArrayList<>();
        for (int i = 0; i < abnormalDataList.size(); i++) {
            String siteName = abnormalDataList.get(i).getSiteName();
            for (int j = 0; j < siteList.size(); j++) {
                // 根据站点名查询站点坐标
                if (siteName.equals(siteList.get(j).getName())) {
                    double lat = siteList.get(j).getLatitude();
                    double lon = siteList.get(j).getLongitude();
                    for (int m = 0; m < riverNodeList.size(); m++) {
                        RiverNodeInfo riverNodeInfo = riverNodeList.get(m);
                        if (riverNodeInfo.getLatitude() == lat && riverNodeInfo.getLongitude() == lon) {
                            nodePositionList.add(m);
                        }
                    }
                }
            }
        }

//        int[] nodePositionArray = new int[nodePositionList.size()];
//        for (int i = 0; i < nodePositionList.size(); i++) {
//            nodePositionArray[i] = nodePositionList.get(i);
//        }
        return nodePositionList;
    }

    /**
     * 污染物降解公式
     *
     * @param c1 初始污染物浓度
     * @param c2 正常浓度
     * @param k  降解系数
     * @param v1 首结点流速
     * @param v2 尾结点流速
     * @return 扩散距离
     */
    public static double degradationFormula(double c1, double c2, double k, double v1, double v2) {
        double v = (v1 + v2) / 2;
        double t = (c1 / c2 - 1) / k;
        return v * t;
    }

    /**
     * 计算污染物降解到正常值的扩散距离
     */
    public static List<Double> analysisDistance(Context context,
                                                List<AbnormalData> abnormalDataList,
                                                List<ParameterInfo> parameterList) {
        List<Double> distanceList = new ArrayList<>();
        for (int i = 0; i < abnormalDataList.size(); i++) {
            AbnormalData abnormalData = abnormalDataList.get(i);
            String parameterName = abnormalData.getParameterName();
            double value = 0;
            double upper = 0;
            double lower = 0;
            double degradation = 0;
            if (context.getResources().getString(R.string.temperature).equals(parameterName)) {
                value = abnormalData.getTemperatureValue();
                upper = parameterList.get(0).getUpper();
                lower = parameterList.get(0).getLower();
                degradation = parameterList.get(0).getDegradation();
            } else if (context.getResources().getString(R.string.ph).equals(parameterName)) {
                value = abnormalData.getPhValue();
                upper = parameterList.get(1).getUpper();
                lower = parameterList.get(1).getLower();
                degradation = parameterList.get(1).getDegradation();
            } else if (context.getResources().getString(R.string.dissolved_oxygen).equals(parameterName)) {
                value = abnormalData.getDissolvedOxygenValue();
                upper = parameterList.get(2).getUpper();
                lower = parameterList.get(2).getLower();
                degradation = parameterList.get(2).getDegradation();
            } else if (context.getResources().getString(R.string.turbidity).equals(parameterName)) {
                value = abnormalData.getTurbidityValue();
                upper = parameterList.get(3).getUpper();
                lower = parameterList.get(3).getLower();
                degradation = parameterList.get(3).getDegradation();
            } else if (context.getResources().getString(R.string.conductivity).equals(parameterName)) {
                value = abnormalData.getConductivityValue();
                upper = parameterList.get(4).getUpper();
                lower = parameterList.get(4).getLower();
                degradation = parameterList.get(4).getDegradation();
            } else if (context.getResources().getString(R.string.blue_green_algae).equals(parameterName)) {
                value = abnormalData.getBlueGreenAlgaeValue();
                upper = parameterList.get(5).getUpper();
                lower = parameterList.get(5).getLower();
                degradation = parameterList.get(5).getDegradation();
            } else if (context.getResources().getString(R.string.chlorophyll).equals(parameterName)) {
                value = abnormalData.getChlorophyllValue();
                upper = parameterList.get(6).getUpper();
                lower = parameterList.get(6).getLower();
                degradation = parameterList.get(6).getDegradation();
            } else if (context.getResources().getString(R.string.ammonia_nitrogen).equals(parameterName)) {
                value = abnormalData.getAmmoniaNitrogenValue();
                upper = parameterList.get(7).getUpper();
                lower = parameterList.get(7).getLower();
                degradation = parameterList.get(7).getDegradation();
            }
            double v = abnormalData.getWaterFlowRate();

            double distance;
            if (value > 0) {
                value += upper;
                distance = degradationFormula(value, upper, degradation, v, v);
            } else {
                value = lower - value;
                distance = degradationFormula(value, lower, degradation, v, v);
            }
            distanceList.add(distance);
        }
        return distanceList;
    }

    /**
     * 计算污染扩散的路径的id
     */
    public static List<List<List<Integer>>> analysisPollutionPath(Integer[] idArray,
                                                                  double[][] riverNodeRelation,
                                                                  List<Integer> startPositionList,
                                                                  List<Double> distanceList) {
        GraphUtil<Integer> graph = new GraphUtil<>(idArray, riverNodeRelation);
        List<List<List<Integer>>> pollutantPathLists = new ArrayList<>();
        for (int i = 0; i < startPositionList.size(); i++) {
            List<List<Integer>> pollutantPathList =
                    graph.startSearch(startPositionList.get(i), distanceList.get(i));
            pollutantPathLists.add(pollutantPathList);
        }
        return pollutantPathLists;
    }

    /**
     * 将id转换为坐标
     */
    public static List<List<List<LatLng>>> analysisIdToLatLng(List<RiverNodeInfo> riverNodeList,
                                                              List<List<List<Integer>>> idPathLists) {
        List<List<List<LatLng>>> pollutantPathLists = new ArrayList<>();
        for (int i = 0; i < idPathLists.size(); i++) {
            List<List<Integer>> idPathList = idPathLists.get(i);
            List<List<LatLng>> pollutantPathList = new ArrayList<>();
            for (int j = 0; j < idPathList.size(); j++) {
                List<Integer> idPath = idPathList.get(j);
                List<LatLng> pollutantPath = new ArrayList<>();
                for (int m = 0; m < idPath.size(); m++) {
                    int id = idPath.get(m);
                    for (int n = 0; n < riverNodeList.size(); n++) {
                        if (id == riverNodeList.get(n).getId()) {
                            LatLng latLng = new LatLng(riverNodeList.get(n).getLatitude(),
                                    riverNodeList.get(n).getLongitude());
                            pollutantPath.add(latLng);
                        }
                    }
                }
                pollutantPathList.add(pollutantPath);
            }
            pollutantPathLists.add(pollutantPathList);
        }
        return pollutantPathLists;
    }

}
