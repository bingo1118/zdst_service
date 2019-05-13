package com.cloudfire.entity;

public class ElectricValueBean {
	 private int electricType;
     private int id;
     private String value="";
     private int ElectricThreshold;

     public int getId() {
         return id;
     }

     public int getElectricThreshold() {
         return ElectricThreshold;
     }

     public void setElectricThreshold(int electricThreshold) {
         ElectricThreshold = electricThreshold;
     }

     public void setId(int id) {
         this.id = id;
     }

     public String getValue() {
         return value;
     }

     public void setValue(String value) {
         this.value = value;
     }

     public int getElectricType() {
         return electricType;
     }

     public void setElectricType(int electricType) {
         this.electricType = electricType;
     }
}
