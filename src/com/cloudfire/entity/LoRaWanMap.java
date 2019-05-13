package com.cloudfire.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoRaWanMap {
	private final static Log log = LogFactory.getLog(LoRaWanMap.class);
	private static LoRaWanMap loRaWanMap = null;
	private Map<String, Long> map = new HashMap<String, Long>();//mac time
	private List<String> loraWanList = new ArrayList<String>();
	
	//����˽�л� ����
	private LoRaWanMap(){}
	
	/**
	 * @Description: ��ȡΨһʵ��
	 */
	public static LoRaWanMap newInstance(){
		if(loRaWanMap == null){
			log.debug("loRaWanMap������ȡ---");
			loRaWanMap = new LoRaWanMap();
		}
		return loRaWanMap;
	}
	
	public void addTime(String key, Long mac){
		this.map.put(key, mac);
	}
	
	public Map<String, Long> getMap(){
		return this.map;
	}
	
	public Long getTime(String key){
		if(map.containsKey(key)){
			return this.map.get(key);
		}else{
			return 0l;
		}
		
	}
	
	public boolean ifOffline(String mac){
		return this.loraWanList.contains(mac);
	}
	
	public void removeRepeater(String mac){
		this.loraWanList.remove(mac);
	}
	
	public void addRepeater(String mac){
		this.loraWanList.add(mac);
	}
}
