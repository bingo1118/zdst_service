package com.cloudfire.thread;

import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.GpsInfoDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.GpsInfoDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.entity.LoRaWanMap;
import com.cloudfire.until.GetTime;

public class GpsTimeThread extends TimerTask{
	private final static Log log = LogFactory.getLog(GpsTimeThread.class);
	private SmokeLineDao mSmokeLineDao;
	@Override
	public void run() {
		GpsInfoDao gpsdao = new GpsInfoDaoImpl();
		gpsdao.ifLossGpsEqument();
		/*Map<String,Long> map = LoRaWanMap.newInstance().getMap();
		for(String key:map.keySet()){
			String loraMac = key;
			long beforeTime = map.get(key);
			boolean result = GetTime.ifOffLine(beforeTime);
			if(result){
				mSmokeLineDao = new SmokeLineDaoImpl();
				mSmokeLineDao.smokeOffLine(loraMac, 0);
			}
		}*/
	}

}
