package com.liuwan.mydesign.bean;

/**
 * Created by liuwan on 2016/11/20.
 * 按站点分类处理后的污染物超标数据及鉴定污染等级
 */
public class AbnormalData {
    // 站点名称
    private String siteName;
    // 温度超标值
    private double temperatureValue;
    // PH超标值
    private double phValue;
    // 溶解氧超标值
    private double dissolvedOxygenValue;
    // 浊度超标值
    private double turbidityValue;
    // 电导率超标值
    private double conductivityValue;
    // 蓝绿藻超标值
    private double blueGreenAlgaeValue;
    // 叶绿素超标值
    private double chlorophyllValue;
    // 氨氮超标值
    private double ammoniaNitrogenValue;
    // 最大超标污染物
    private String parameterName;
    // 最大超标级别
    private int level;
    // 水流速
    private double waterFlowRate;

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public double getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(double temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public double getPhValue() {
        return phValue;
    }

    public void setPhValue(double phValue) {
        this.phValue = phValue;
    }

    public double getDissolvedOxygenValue() {
        return dissolvedOxygenValue;
    }

    public void setDissolvedOxygenValue(double dissolvedOxygenValue) {
        this.dissolvedOxygenValue = dissolvedOxygenValue;
    }

    public double getTurbidityValue() {
        return turbidityValue;
    }

    public void setTurbidityValue(double turbidityValue) {
        this.turbidityValue = turbidityValue;
    }

    public double getConductivityValue() {
        return conductivityValue;
    }

    public void setConductivityValue(double conductivityValue) {
        this.conductivityValue = conductivityValue;
    }

    public double getBlueGreenAlgaeValue() {
        return blueGreenAlgaeValue;
    }

    public void setBlueGreenAlgaeValue(double blueGreenAlgaeValue) {
        this.blueGreenAlgaeValue = blueGreenAlgaeValue;
    }

    public double getChlorophyllValue() {
        return chlorophyllValue;
    }

    public void setChlorophyllValue(double chlorophyllValue) {
        this.chlorophyllValue = chlorophyllValue;
    }

    public double getAmmoniaNitrogenValue() {
        return ammoniaNitrogenValue;
    }

    public void setAmmoniaNitrogenValue(double ammoniaNitrogenValue) {
        this.ammoniaNitrogenValue = ammoniaNitrogenValue;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getWaterFlowRate() {
        return waterFlowRate;
    }

    public void setWaterFlowRate(double waterFlowRate) {
        this.waterFlowRate = waterFlowRate;
    }

    public AbnormalData() {
        super();
    }

    public AbnormalData(String siteName, double temperatureValue, double phValue, double dissolvedOxygenValue, double turbidityValue, double conductivityValue, double blueGreenAlgaeValue, double chlorophyllValue, double ammoniaNitrogenValue, String parameterName, int level, double waterFlowRate) {
        this.siteName = siteName;
        this.temperatureValue = temperatureValue;
        this.phValue = phValue;
        this.dissolvedOxygenValue = dissolvedOxygenValue;
        this.turbidityValue = turbidityValue;
        this.conductivityValue = conductivityValue;
        this.blueGreenAlgaeValue = blueGreenAlgaeValue;
        this.chlorophyllValue = chlorophyllValue;
        this.ammoniaNitrogenValue = ammoniaNitrogenValue;
        this.parameterName = parameterName;
        this.level = level;
        this.waterFlowRate = waterFlowRate;
    }

    @Override
    public String toString() {
        return "AbnormalData{" +
                "siteName='" + siteName + '\'' +
                ", temperatureValue=" + temperatureValue +
                ", phValue=" + phValue +
                ", dissolvedOxygenValue=" + dissolvedOxygenValue +
                ", turbidityValue=" + turbidityValue +
                ", conductivityValue=" + conductivityValue +
                ", blueGreenAlgaeValue=" + blueGreenAlgaeValue +
                ", chlorophyllValue=" + chlorophyllValue +
                ", ammoniaNitrogenValue=" + ammoniaNitrogenValue +
                ", parameterName='" + parameterName + '\'' +
                ", level=" + level +
                ", waterFlowRate=" + waterFlowRate +
                '}';
    }

}
