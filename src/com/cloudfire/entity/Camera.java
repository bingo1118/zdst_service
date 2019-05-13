package com.cloudfire.entity;

import com.fasterxml.jackson.databind.ser.std.SerializableSerializer;


public class Camera extends SerializableSerializer{
	private String indexCode;
	private String name;
	
	public String getIndexCode() {
		return indexCode;
	}
	public void setIndexCode(String indexCode) {
		this.indexCode = indexCode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
