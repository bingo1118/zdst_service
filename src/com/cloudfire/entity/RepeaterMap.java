package com.cloudfire.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.RePeaterDataDao;
import com.cloudfire.dao.impl.RePeaterDataDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;

public class RepeaterMap {
	private final static Log log = LogFactory.getLog(RepeaterMap.class);
	private static RepeaterMap repeaterMap = null;
//	private Map<String, Long> map = new HashMap<String, Long>();//repeaterMac->heartime
//	private List<String> repeaterList = new ArrayList<String>(); //offline repeater list
	
	//构造私有化 单例
	private RepeaterMap(){}
	
	/**
	 * @Description: 获取唯一实例
	 */
	public static RepeaterMap newInstance(){
		if(repeaterMap == null){
			log.debug("RepeaterMap单例获取---");
			repeaterMap = new RepeaterMap();
		}
		return repeaterMap;
	}
	
	public void addTime(String key, Long heartime){
//		log.debug("保存会话到RepeaterMap单例---map.size=" + map.size());
//		this.map.put(key, heartime);
		if (heartime != 0){ //若是主机心跳，则更新redis主机状态和心跳时间
			Jedis jedis = RedisConnection.getJedis();
			if (jedis!=null) {
				String requestId = UUID.randomUUID().toString().replace("-", "");
				while(!RedisOps.tryGetDistributedLock(jedis, "L"+key, requestId, 10000)){
					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if (RedisOps.exist(jedis,"R"+key)) {
					Repeater rep  = (Repeater) RedisOps.getObject(jedis,"R"+key);
					rep.setNetState(1);
					rep.setHeartime(heartime);
					RedisOps.setObject(jedis,"R"+key, rep);
				} else {
					Repeater rep  = new Repeater();
					rep.setRepeaterMac(key);
					rep.setNetState(1);
					rep.setHeartime(heartime);
					rep.setPowerState(0);
					rep.setPowerChangeTime(0);
					RedisOps.setObject(jedis,"R"+key, rep);
				}
				
				RedisOps.releaseDistributedLock(jedis, "L"+key, requestId);
				jedis.close();
			}
		}
	}
	
//	public Map<String, Long> getMap(){
//		return this.map;
//	}
	
//	public Long getTime(String key){
//		log.debug("获取会话从RepeaterMap单例---key=" + key);
//		if(map.containsKey(key)){
//			return this.map.get(key);
//		}else{
//			return 0l;
//		}
//		
//	}
//	
//	public boolean ifOffline(String mac){
//		return this.repeaterList.contains(mac);
//	}
//	
//	public void removeRepeater(String mac){
//		this.repeaterList.remove(mac);
//	}
//	
//	public void addRepeater(String mac){
//		this.repeaterList.add(mac);
//	}
}
