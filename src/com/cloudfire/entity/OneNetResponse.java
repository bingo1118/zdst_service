package com.cloudfire.entity;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class OneNetResponse {
	private String errno;
	private String error;
	private Map<String,String> data;
	public String getErrno() {
		return errno;
	}
	public void setErrno(String errno) {
		this.errno = errno;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	public String toString(){
		String str = "errno:"+errno+",error:"+error;
		if(data!=null) {
			str +=",data:{";
			Iterator<Entry<String, String>> iterator = data.entrySet().iterator();
			while(iterator.hasNext()){
				Entry<String, String> next = iterator.next();
				str+=next.getKey()+":"+next.getValue();
				if (iterator.hasNext()) {
					str+=",";
				} 
			}
			str+= "}";
		}
		return str;
	}
}
