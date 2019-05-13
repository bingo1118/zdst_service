package com.cloudfire.entity;

import java.util.List;

public class ElectricValue {
	 /**
     * electricTime : 2016-11-15 11:51:42
     * electricType : 6
     * electricValue : [{"id":1,"value":"222.190002"},{"id":2,"value":""},{"id":3,"value":""}]
     */

    private String electricTime="";
    private int electricType;
    /**
     * id : 1
     * value : 222.190002
     */

    private List<ElectricValueBean> electricValue=null;

    public String getElectricTime() {
        return electricTime;
    }

    public void setElectricTime(String electricTime) {
        this.electricTime = electricTime;
    }

    public int getElectricType() {
        return electricType;
    }

    public void setElectricType(int electricType) {
        this.electricType = electricType;
    }

    public List<ElectricValueBean> getElectricValue() {
        return electricValue;
    }

    public void setElectricValue(List<ElectricValueBean> electricValue) {
        this.electricValue = electricValue;
    }
}
