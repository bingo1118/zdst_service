package com.cloudfire.entity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

public class SessionMap {
	private final static Log log = LogFactory.getLog(SessionMap.class);
	private static SessionMap sessionMap = null;
	private Map<String, IoSession>map = new HashMap<String, IoSession>();
	
	//����˽�л� ����
	private SessionMap(){}
	
	/**
	 * @Description: ��ȡΨһʵ��
	 */
	public static SessionMap newInstance(){
		if(sessionMap == null){
			log.debug("SessionMap������ȡ---");
			sessionMap = new SessionMap();
		}
		return sessionMap;
	}
	
	
	/**
	 * @Description: ����session�Ự
	 * @author whl
	 * @date 2014-9-29 ����1:31:05
	 */
	public void addSession(String key, IoSession session){
		log.debug("����Ự��SessionMap����---map.size=" + map.size());
		this.map.put(key, session);
	}
	
	/**
	 * @Description: ����key���һ����session
	 * @author whl
	 * @date 2014-9-29 ����1:31:55
	 */
	public IoSession getSession(String key){
		log.debug("��ȡ�Ự��SessionMap����---key=" + key);
		return this.map.get(key);
	}
	
	/**
	 * @Description: ������Ϣ���ͻ���
	 * @author whl
	 * @date 2014-9-29 ����1:57:51
	 */
	public void sendMessage(String[] keys, Object message){
		for(String key : keys){
			IoSession session = getSession(key);
			log.debug("��������Ϣ���ͻ���Session---key=" + key + "----------��Ϣ=" + message);
			if(session == null){
				return;
			}
			session.write(message);
			
		}
	}
}
