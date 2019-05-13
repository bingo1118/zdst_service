package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmokeMap2 {
	private final static Log log = LogFactory.getLog(SmokeMap2.class);
	private static SmokeMap2 smokeMap = null;
	private Map<String, Map<String,Long>> map = new HashMap<String, Map<String,Long>>();//mac time
	
	//构造私有化 单例
	private SmokeMap2(){}
	
	/**
	 * @Description: 获取唯一实例
	 */
	public static SmokeMap2 newInstance(){
		if(smokeMap == null){
			log.debug("SmokeMap2单例获取---");
			smokeMap = new SmokeMap2();
		}
		return smokeMap;
	}
	
	public void addSmokeMap(String key, Map<String,Long> list){
		log.debug("保存会话到SmokeMap2单例---map.size=" + map.size());
		this.map.put(key, list);
	}
	
	public Map<String, Map<String,Long>> getSmokeMap(){
		return this.map;
	}
	
	public void removeMap(String key){
		if(this.map.containsKey(key)){
			this.map.remove(key);
		}
	}
}
