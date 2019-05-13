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
//		Map<String,Long> map = RepeaterMap.newInstance().getMap();//��ȡ����
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
//						mSmokeLineDao.RepeaterOffLine(repeaterMac,0); //�����豸�����ߣ�����״̬
//						RepeaterMap.newInstance().addRepeater(repeaterMac);
//						SmokeMap.newInstance().removeMap(repeaterMac);
//					}
//					
//				}
//			}
//		}
		
		mSmokeLineDao = new SmokeLineDaoImpl();
		//redis������������״̬���߳�
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
					int parentId = areaDaoImpl.getSuperAreaIdByRepeater(repeaterMac); //���η�ְԺ����������
					if(parentId!=162&&areaid!=27&&areaid!=34&&areaid!=2052){//(27,34,2052)
						if (RedisOps.tryGetDistributedLock(jedis, "L"+repeaterMac, requestId, 10000)){ //��ȡ�����Զ�����ʱ����Ϊ10s
							if (RedisOps.exist(jedis,repMac)){
								Repeater rep  = (Repeater)RedisOps.getObject(jedis,repMac);
								//�����������뵱ǰʱ���ֵ����14min,������״̬Ϊ����
								if (rep.getNetState()==1&&System.currentTimeMillis()-rep.getHeartime()>12*60*1000) {
									rep.setNetState(0);
									RedisOps.setObject(jedis,repMac, rep);
									
									mSmokeLineDao.RepeaterOffLine2(repeaterMac,0);
									
									//	������������зǵ����������豸�������豸�б�
									List<String> macs =SmokeLineDaoImpl.getMacs(repeaterMac);
									//���������������ʵ�������һ����
									RedisOps.setList(jedis,repeaterMac, macs);
								}
							}
							
							RedisOps.releaseDistributedLock(jedis, "L"+repMac, requestId); //�ͷ���
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
