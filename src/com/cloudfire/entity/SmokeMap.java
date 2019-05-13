package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.impl.SmokeLineDaoImpl;

public class SmokeMap {
	private final static Log log = LogFactory.getLog(SmokeMap.class);
	private static SmokeMap smokeMap = null;
	private Map<String, Map<String,Long>> map = new HashMap<String, Map<String,Long>>();//mac time
	
	//构造私有化 单例
	private SmokeMap(){}
	
	/**
	 * @Description: 获取唯一实例
	 */
	public static SmokeMap newInstance(){
		if(smokeMap == null){
			log.debug("SmokeMap单例获取---");
			smokeMap = new SmokeMap();
		}
		return smokeMap;
	}
	
	public void addSmokeMap(String key, Map<String,Long> list){
		log.debug("保存会话到SmokeMap单例---map.size=" + map.size());
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
	
	//更新SmokeMap对应的repeater下的设备时间表
	public void updateSmokeMap(String repeater,String smokeMac){
		 //非电气设备的添加才要对内存中的smokeMap操作  edit by yfs @12/8/2017 9:16
		Long addTime = System.currentTimeMillis();
		Map<String,Long> macTime = map.get(repeater);
		if(macTime == null){
			SmokeLineDaoImpl mSmokeLineDao = new SmokeLineDaoImpl();
			macTime = mSmokeLineDao.RepeaterLoss(repeater);
		}
		
		macTime.put(smokeMac, addTime);
		
		map.put(repeater, macTime);
	
	}
}
