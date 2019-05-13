package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

public class TimerMapReSet {
	private final static Log log = LogFactory.getLog(TimerMapReSet.class);
	private static TimerMapReSet TimerMapReSet = null;
	private Map<String, Timer>map = new HashMap<String, Timer>();
	
	//构造私有化 单例
	private TimerMapReSet(){}
	
	/**
	 * @Description: 获取唯一实例
	 */
	public static TimerMapReSet newInstance(){
		if(TimerMapReSet == null){
			log.debug("TimerMap单例获取---");
			TimerMapReSet = new TimerMapReSet();
		}
		return TimerMapReSet;
	}
	
	public void addTimer(String key, Timer timer){
		log.debug("保存会话到SessionMap单例---map.size=" + map.size());
		this.map.put(key, timer);
	}

	public Timer getTimer(String key){
		log.debug("获取会话从SessionMap单例---key=" + key);
		return this.map.get(key);
	}
	

}
