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
	
	//����˽�л� ����
	private TimerMapReSet(){}
	
	/**
	 * @Description: ��ȡΨһʵ��
	 */
	public static TimerMapReSet newInstance(){
		if(TimerMapReSet == null){
			log.debug("TimerMap������ȡ---");
			TimerMapReSet = new TimerMapReSet();
		}
		return TimerMapReSet;
	}
	
	public void addTimer(String key, Timer timer){
		log.debug("����Ự��SessionMap����---map.size=" + map.size());
		this.map.put(key, timer);
	}

	public Timer getTimer(String key){
		log.debug("��ȡ�Ự��SessionMap����---key=" + key);
		return this.map.get(key);
	}
	

}
