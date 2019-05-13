package com.cloudfire.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.LogFactory;
import org.mortbay.log.Log;

import redis.clients.jedis.Jedis;
import sun.util.logging.resources.logging;

import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.SmokeMap;
//import com.cloudfire.entity.SmokeMap2;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.Indexes;
import com.cloudfire.until.LogInfo;
import com.cloudfire.until.Utils;

public class SmokeLineThread extends Thread{
	private RePeaterData mRePeaterData;
//	private SmokeMap mSmokeMap;
	private long nowTime;
	private SmokeLineDao mSmokeLineDao;
	private static final org.apache.commons.logging.Log log=LogFactory.getLog(SmokeLineThread.class);
	
	public SmokeLineThread(RePeaterData mRePeaterData,long nowTime){
//		mSmokeMap = SmokeMap.newInstance();
		this.mRePeaterData = mRePeaterData;
		this.nowTime = nowTime;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		Map<String, Map<String,Long>> map = mSmokeMap.getSmokeMap();
		mSmokeLineDao = new SmokeLineDaoImpl();
		String rePeatermac = mRePeaterData.getRepeatMac();
		boolean isChanged = false;
		Map<String,Long> newSmokes = mRePeaterData.getFireMacList();
	
		//获取redis要存储的离线设备列表
		@SuppressWarnings("unchecked")
		List<String> newOffSmokes = new ArrayList(newSmokes.keySet()); 
		
		Jedis jedis = RedisConnection.getJedis();
		if (jedis!=null) {
			String requestId = UUID.randomUUID().toString().replace("-", "");
			while(!RedisOps.tryGetDistributedLock(jedis, "L"+rePeatermac, requestId, 10000)){
				try {
					Thread.currentThread().sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			List<String> offSmokes = RedisOps.getList(jedis,rePeatermac);
			//获取两个离线列表中不变设备的索引
			Indexes common_indexes = Utils.getCommons(newOffSmokes, offSmokes);
			List<String> offline = new ArrayList<String>();
			List<String> online = new ArrayList<String>();
			//从新的离线列表里面获取新的离线设备列表并使之离线
			for(int i=0;i<newOffSmokes.size();i++){
				if (!common_indexes.getLst().contains(i)) {
					offline.add(newOffSmokes.get(i));
				}
			}
			//从旧的离线列表里面将重新上线的上线
			for(int i=0;i<offSmokes.size();i++){
				if (!common_indexes.getLst2().contains(i)) {
					online.add(offSmokes.get(i));
				}
			}
			if (!(online.size()==0&&offline.size()==0)){
				isChanged=true;
				RedisOps.setList(jedis,rePeatermac, newOffSmokes);
			}
			mSmokeLineDao.smokeOffLine3(newOffSmokes, 0);
			mSmokeLineDao.smokeOffLine3(online, 1);
			
			RedisOps.releaseDistributedLock(jedis, "L"+rePeatermac, requestId);
			jedis.close();
		}
		
//		if(map.containsKey(rePeatermac)){
//			
//			//测试使用代码段
//			/*log.debug("微微 已经有中继器 size:"+map.size());
//			Map<String,Long> mapSmoke = map.get(rePeatermac);
//			isChanged = Utils.smokeChanged(newSmokes,mapSmoke);
//			if(isChanged){
//				mSmokeMap.addSmokeMap(rePeatermac, newSmokes);
//				List<String> lossList=Utils.getLossSmokeList(newSmokes, mapSmoke);
//				List<String> upList=Utils.getUpSmokeList(newSmokes, mapSmoke);
//				
//				log.debug("微微 有改变 烟感");
//				mSmokeLineDao.RepeaterOffLine(rePeatermac,1);
//				for(String newSmokeKey:newSmokes.keySet()){
//					mSmokeLineDao.smokeOffLine(newSmokeKey, 0);
//					log.debug("有改变 烟感 烟感：掉线的 :"+newSmokeKey);
//				}
//				if(lossList.size()>0){
//					for(String lossKey:lossList){
//					
//						mSmokeLineDao.smokeLossUp(rePeatermac, lossKey, "loss");
//					}
//				}
//				if(upList.size()>0){
//					for(String upKey:upList){
//					
//						mSmokeLineDao.smokeLossUp(rePeatermac, upKey, "up");
//					}
//				}
//				
//			}
//			else{
//				log.debug("微微 没有改变 烟感");
//			}*/
//			
//			
//			
//			//正式用代码
//			Map<String,Long> mapSmoke = map.get(rePeatermac);
//			isChanged = Utils.smokeChanged(newSmokes,mapSmoke);
//			for (Map.Entry<String,Long> entry : newSmokes.entrySet()) {
//				String key = entry.getKey();
//				boolean keyIsExit = mapSmoke.containsKey(key);
//				if(!keyIsExit){
//					mapSmoke.put(key, entry.getValue());
//				}
//			}
//		
//			Iterator<String> iterator = mapSmoke.keySet().iterator();    
//			synchronized (iterator) {
//			while (iterator.hasNext()) {   
//			    String smokeMac = (String) iterator.next();   
//			    boolean conKey = newSmokes.containsKey(smokeMac);
//				mSmokeLineDao = new SmokeLineDaoImpl();
//				if(conKey){
//					long beforeTime = mapSmoke.get(smokeMac);
//					boolean result = GetTime.ifSmokeOffLine(beforeTime, nowTime);
//					if(result){
//						
//						mSmokeLineDao.smokeOffLine(smokeMac, 0);
////						mSmokeLineDao.smokeLossUp(rePeatermac, smokeMac, "loss");
//						mapSmoke.put(smokeMac, nowTime);
//						
//					}
//				}else{
//					mSmokeLineDao.smokeOffLine(smokeMac, 1);
////					mSmokeLineDao.smokeLossUp(rePeatermac, smokeMac, "up");
//					iterator.remove();
//					mapSmoke.remove(smokeMac);
//					
//				}
//			 } 
//			}
//			mSmokeMap.addSmokeMap(rePeatermac, mapSmoke);
//		}else{
//			newSmokes = mSmokeLineDao.RepeaterLoss(rePeatermac);
//			mSmokeMap.addSmokeMap(rePeatermac, newSmokes);
//			/*mSmokeLineDao.RepeaterOffLine(rePeatermac,1);
//			log.debug("第一次心跳 微微");
//			for(String newSmokeKey:newSmokes.keySet()){
//				mSmokeLineDao.smokeOffLine(newSmokeKey, 0);
//				log.debug("烟感：掉线的 :"+newSmokeKey);
//			}
//			log.debug("xintiao====================1");*/	
//			isChanged=true;
//		}
		if(isChanged){
			recordLog(newSmokes,rePeatermac);
		}
	}
		
	
	private void recordLog(Map<String,Long> newSmokes,String repeater){
		StringBuffer newLog = new StringBuffer();
		String recordTime = GetTime.ConvertTimeByLong();
		newLog.append(recordTime+"  ");
		if(newSmokes!=null&&newSmokes.size()>0){
			for (String key : newSmokes.keySet()) {
				newLog.append(key+"  ");
			}
		}
		LogInfo.appendLog(newLog.toString(), repeater);
	}
}
