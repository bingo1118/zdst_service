package com.cloudfire.entity;

import java.util.List;

public class AllSmokeEntity {
    /**
     * error : ��ȡ�̸гɹ���
     * errorCode : 0
     * smoke : [{"address":"������ �����148��","areaName":"�Ϸ��ɳ���","camera":{"areaName":"","cameraAddress":"","cameraId":"","cameraName":"","cameraPwd":"","latitude":"","longitude":"","placeType":"","principal1":"","principal1Phone":"","principal2":"","principal2Phone":""},"deviceType":1,"ifDealAlarm":1,"latitude":"22.724359","longitude":"113.943216","mac":"1C491730","name":"����������","netState":1,"placeType":"������","placeeAddress":"","principal1":"������","principal1Phone":"13510914166","principal2":"","principal2Phone":"","repeater":"03091620"},{"address":"�й��㶫ʡ�����з�خ��ʯ�ж�·","areaName":"��خ����ʯ���Ӳ�ҵ԰��","camera":{"areaName":"","cameraAddress":"","cameraId":"","cameraName":"","cameraPwd":"","latitude":"","longitude":"","placeType":"","principal1":"","principal1Phone":"","principal2":"","principal2Phone":""},"deviceType":1,"ifDealAlarm":1,"latitude":"23.016331","longitude":"113.294726","mac":"360D43CC","name":"����1","netState":0,"placeType":"������","placeeAddress":"","principal1":"��־ΰ","principal1Phone":"18312712709","principal2":"","principal2Phone":"","repeater":"03111620"},{"address":"�й��㶫ʡ�����з�خ��ʯ����ҵ·","areaName":"��خ����ʯ���Ӳ�ҵ԰��","camera":{"areaName":"","cameraAddress":"","cameraId":"","cameraName":"","cameraPwd":"","latitude":"","longitude":"","placeType":"","principal1":"","principal1Phone":"","principal2":"","principal2Phone":""},"deviceType":1,"ifDealAlarm":1,"latitude":"23.018769","longitude":"113.294216","mac":"536A1831","name":"��ֿ�����1","netState":1,"placeType":"����","placeeAddress":"","principal1":"%E8%8B%8F%E5%85%88%E7%94%9F","principal1Phone":"13710800611","principal2":"","principal2Phone":"","repeater":"03111620"},{"address":"�й��㶫ʡ�����з�خ��ʯ����ҵ·","areaName":"��خ����ʯ���Ӳ�ҵ԰��","camera":{"areaName":"","cameraAddress":"","cameraId":"","cameraName":"","cameraPwd":"","latitude":"","longitude":"","placeType":"","principal1":"","principal1Phone":"","principal2":"","principal2Phone":""},"deviceType":1,"ifDealAlarm":0,"latitude":"23.01857","longitude":"113.294144","mac":"40461730","name":"��ֿ�����2","netState":1,"placeType":"����","placeeAddress":"","principal1":"������","principal1Phone":"13710800611","principal2":"","principal2Phone":"","repeater":"03111620"}]
     */

    private String error="��ȡ�̸�ʧ��";
    private int errorCode=2;
    /**
     * address : ������ �����148��
     * areaName : �Ϸ��ɳ���
     * camera : {"areaName":"","cameraAddress":"","cameraId":"","cameraName":"","cameraPwd":"","latitude":"","longitude":"","placeType":"","principal1":"","principal1Phone":"","principal2":"","principal2Phone":""}
     * deviceType : 1
     * ifDealAlarm : 1
     * latitude : 22.724359
     * longitude : 113.943216
     * mac : 1C491730
     * name : ����������
     * netState : 1
     * placeType : ������
     * placeeAddress : 
     * principal1 : ������
     * principal1Phone : 13510914166
     * principal2 : 
     * principal2Phone : 
     * repeater : 03091620
     */

    private List<SmokeBean> smoke=null;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<SmokeBean> getSmoke() {
        return smoke;
    }

    public void setSmoke(List<SmokeBean> smoke) {
        this.smoke = smoke;
    }
}
