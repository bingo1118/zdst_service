package com.cloudfire.entity;

/** ��γ�ȵ��װ
 * Created by �������� on 2017-03-22.
 */
public class LngLat {
    private double longitude;//����
    private double lantitude;//ά��
 
    public LngLat() {
    }
 
    public LngLat(double longitude, double lantitude) {
        this.longitude = longitude;
        this.lantitude = lantitude;
    }
 
    public double getLongitude() {
        return longitude;
    }
 
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
 
    public double getLantitude() {
        return lantitude;
    }
 
    public void setLantitude(double lantitude) {
        this.lantitude = lantitude;
    }
 
    @Override
    public String toString() {
        return "LngLat{" +
                "longitude=" + longitude +
                ", lantitude=" + lantitude +
                '}';
    }
}
