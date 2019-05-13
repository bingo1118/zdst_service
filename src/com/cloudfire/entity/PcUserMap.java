package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PcUserMap {
	private final static Log log = LogFactory.getLog(PcUserMap.class);
	private static PcUserMap pcUserMapMap = null;
	private Map<String, String>map = new HashMap<String, String>();
	
	//构造私有化 单例
	private PcUserMap(){}
	
	/**
	 * @Description: 获取唯一实例
	 */
	public static PcUserMap newInstance(){
		if(pcUserMapMap == null){
			log.debug("SessionMap单例获取---");
			pcUserMapMap = new PcUserMap();
		}
		return pcUserMapMap;
	}
	
	public void addUserMac(String key, String mac){
		log.debug("保存会话到SessionMap单例---map.size=" + map.size());
		this.map.put(key, mac);
	}
	
	public String getUserMac(String key){
		log.debug("获取会话从SessionMap单例---key=" + key);
		return this.map.get(key);
	}
	
}
