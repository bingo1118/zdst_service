package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PcUserMap {
	private final static Log log = LogFactory.getLog(PcUserMap.class);
	private static PcUserMap pcUserMapMap = null;
	private Map<String, String>map = new HashMap<String, String>();
	
	//����˽�л� ����
	private PcUserMap(){}
	
	/**
	 * @Description: ��ȡΨһʵ��
	 */
	public static PcUserMap newInstance(){
		if(pcUserMapMap == null){
			log.debug("SessionMap������ȡ---");
			pcUserMapMap = new PcUserMap();
		}
		return pcUserMapMap;
	}
	
	public void addUserMac(String key, String mac){
		log.debug("����Ự��SessionMap����---map.size=" + map.size());
		this.map.put(key, mac);
	}
	
	public String getUserMac(String key){
		log.debug("��ȡ�Ự��SessionMap����---key=" + key);
		return this.map.get(key);
	}
	
}
