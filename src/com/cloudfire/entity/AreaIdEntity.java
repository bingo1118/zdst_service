package com.cloudfire.entity;

import java.util.List;

public class AreaIdEntity {
	 /**
     * error : ��ȡ����id�ɹ�
     * errorCode : 0
     * smoke : [{"areaId":"14","areaName":"�Ϸ��ɳ���"},{"areaId":"15","areaName":"����Ƭ��"}]
     */

    private String error="";
    private int errorCode;
    /**
     * areaId : 14
     * areaName : �Ϸ��ɳ���
     */

    private List<AreaBean> smoke=null;

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

    public List<AreaBean> getSmoke() {
        return smoke;
    }

    public void setSmoke(List<AreaBean> smoke) {
        this.smoke = smoke;
    }

}
