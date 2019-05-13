package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

public class TimerMap {
	private final static Log log = LogFactory.getLog(Timer.class);
	private static TimerMap timerMap = null;
	private Map<String, Timer>map = new HashMap<String, Timer>();
	
	//����˽�л� ����
	private TimerMap(){}
	
	/**
	 * @Description: ��ȡΨһʵ��
	 */
	public static TimerMap newInstance(){
		if(timerMap == null){
			log.debug("TimerMap������ȡ---");
			timerMap = new TimerMap();
		}
		return timerMap;
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
