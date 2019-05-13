package com.cloudfire.entity;

import org.apache.commons.lang.StringUtils;

import com.cloudfire.until.Utils;

public class LoginEntity {
	/**
     * error : 登录成功）
     * errorCode : 0
     * name : 1
     * privilege : 3
     */

    private String error="没有此用户";
    private int errorCode=2;
    private String name="";
    private int privilege;
    private int privId;
    private String toKen;
    private String userId;
    private String salt;
    private String isCanCutEletr="1";
    
    public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getToKen() {
		if(StringUtils.isBlank(this.toKen)){
			toKen = Utils.userMd5.get(this.userId);
		}
		return toKen;
	}

	public void setToKen(String toKen) {
		this.toKen = toKen;
	}

	public int getPrivId() {
		return privId;
	}

	public void setPrivId(int privId) {
		this.privId = privId;
	}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

	public String getIsCanCutEletr() {
		return isCanCutEletr;
	}

	public void setIsCanCutEletr(String isCanCutEletr) {
		this.isCanCutEletr = isCanCutEletr;
	}
}
