/**
 * ионГ11:41:38
 */
package com.cloudfire.entity.query;

import java.util.List;

/**
 * @author cheng
 *2017-6-16
 *ионГ11:41:38
 */
public class MyHttpResult {
	private String error="";
	private String username="";
	private String isCanCutEletr="1";
    private int errorCode;
    public List<String> list ;
    
    
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public List<String> getList() {
		return list;
	}
	public void setList(List<String> list) {
		this.list = list;
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
	public String getIsCanCutEletr() {
		return isCanCutEletr;
	}
	public void setIsCanCutEletr(String isCanCutEletr) {
		this.isCanCutEletr = isCanCutEletr;
	}
}
