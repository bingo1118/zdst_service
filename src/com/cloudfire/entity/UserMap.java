package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

public class UserMap {
	private final static Log log = LogFactory.getLog(UserMap.class);
	private static UserMap UserMap = null;
	private Map<String, String>map = new HashMap<String, String>();
	
	//构造私有化 单例
	private UserMap(){}
	public static UserMap newInstance(){
		if(UserMap == null){
			UserMap = new UserMap();
		}
		return UserMap;
	}
	
	public void addUser(String repeater, String user){
		this.map.put(repeater, user);
	}
	
	public String getUser(String repeater){
		return this.map.get(repeater);
	}
	
	public void removeUser(String repeater){
		this.map.remove(repeater);
	}
	
}
