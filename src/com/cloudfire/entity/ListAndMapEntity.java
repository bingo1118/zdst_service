package com.cloudfire.entity;

import java.util.List;
import java.util.Map;

public class ListAndMapEntity {
	List<String> objList;
	Map<Integer,String> objMap;
	Map<Map<Integer,String>,Map<Integer,String>> objMapMap;
	
	public Map<Map<Integer, String>, Map<Integer, String>> getObjMapMap() {
		return objMapMap;
	}
	public void setObjMapMap(
			Map<Map<Integer, String>, Map<Integer, String>> objMapMap) {
		this.objMapMap = objMapMap;
	}
	public List<String> getObjList() {
		return objList;
	}
	public void setObjList(List<String> objList) {
		this.objList = objList;
	}
	public Map<Integer, String> getObjMap() {
		return objMap;
	}
	public void setObjMap(Map<Integer, String> objMap) {
		this.objMap = objMap;
	}
}
