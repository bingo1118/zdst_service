package com.cloudfire.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.LoRaWanMap;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.RepeaterMap;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.until.GetTime;

public class RepeaterTimeTask extends TimerTask{ 
	private final static Log log = LogFactory.getLog(RepeaterTimeTask.class);
	private SmokeLineDao mSmokeLineDao;

	@Override
	public void run() {
		// TODO Auto-generated method stub
//		Map<String,Long> map = RepeaterMap.newInstance().getMap();//获取单例
//		mSmokeLineDao = new SmokeLineDaoImpl();
//		for (Map.Entry<String,Long> entry : map.entrySet()) {  
//			long beforeTime = entry.getValue();
//			String repeaterMac = entry.getKey();
//			if(beforeTime==0){
//				SmokeMap.newInstance().removeMap(repeaterMac);
//			}else{
//				boolean ifOfflined = RepeaterMap.newInstance().ifOffline(repeaterMac);
//				boolean result = GetTime.ifOffLine(beforeTime);
//				if(ifOfflined&&!result){
//					RepeaterMap.newInstance().removeRepeater(repeaterMac);
//				}
//				if(result&&!ifOfflined){
//					log.debug("addRepeater========");
//					AreaDao areaDaoImpl=new AreaDaoImpl();
//					int areaid=areaDaoImpl.getAreaIdByRepeater(repeaterMac);
//					if(areaid!=27&&areaid!=34&&areaid!=2052){//(27,34,2052)
//						mSmokeLineDao.RepeaterOffLine(repeaterMac,0); //更新设备的在线，离线状态
//						RepeaterMap.newInstance().addRepeater(repeaterMac);
//						SmokeMap.newInstance().removeMap(repeaterMac);
//					}
//					
//				}
//			}
//		}
		
		mSmokeLineDao = new SmokeLineDaoImpl();
		//redis更新主机在线状态的线程
		Jedis jedis = RedisConnection.getJedis();
		if (jedis != null){
			Set<String> repeaters = RedisOps.getRepeaters(jedis);
			Iterator<String> it = repeaters.iterator();
			String requestId = UUID.randomUUID().toString().replace("-", "");
			while(it.hasNext()){
				try{
					String repMac = it.next(); //R+repeaterMac
					String repeaterMac=repMac.substring(1); //repeaterMac
					AreaDao areaDaoImpl=new AreaDaoImpl();
					int areaid=areaDaoImpl.getAreaIdByRepeater(repeaterMac);
					int parentId = areaDaoImpl.getSuperAreaIdByRepeater(repeaterMac); //屏蔽番职院的主机掉线
					if(parentId!=162&&areaid!=27&&areaid!=34&&areaid!=2052){//(27,34,2052)
						if (RedisOps.tryGetDistributedLock(jedis, "L"+repeaterMac, requestId, 10000)){ //获取锁，自动过期时间设为10s
							if (RedisOps.exist(jedis,repMac)){
								Repeater rep  = (Repeater)RedisOps.getObject(jedis,repMac);
								//若主机心跳与当前时间差值超过14min,则主机状态为掉线
								if (rep.getNetState()==1&&System.currentTimeMillis()-rep.getHeartime()>12*60*1000) {
									rep.setNetState(0);
									RedisOps.setObject(jedis,repMac, rep);
									
									mSmokeLineDao.RepeaterOffLine2(repeaterMac,0);
									
									//	添加主机下所有非电气非主机设备到离线设备列表
									List<String> macs =SmokeLineDaoImpl.getMacs(repeaterMac);
									//以下两个操作如何实现事务的一致性
									RedisOps.setList(jedis,repeaterMac, macs);
								}
							}
							
							RedisOps.releaseDistributedLock(jedis, "L"+repMac, requestId); //释放锁
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			
			jedis.close();
			
		}
	}

	public static void main(String[] args) {
		String s = "123456789";
		System.out.println(s.substring(1));
	}
	
}
