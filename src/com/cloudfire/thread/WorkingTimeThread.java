package com.cloudfire.thread;

import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.DeviceDao;
import com.cloudfire.dao.impl.DevicesDaoImpl;
import com.cloudfire.until.GetTime;

public class WorkingTimeThread extends TimerTask{
	private final static Log log = LogFactory.getLog(WorkingTimeThread.class);
	
	@Override
	public void run() {
		DeviceDao dev = new DevicesDaoImpl();
		Map<String,String> ulist = dev.getUserWorking();
		String nowTime = GetTime.ConvertTimeByLong().substring(0,11);
		System.out.println("Join to WorkingTimeThread:");
		if(ulist!=null){
			System.out.println(ulist.get("13432316687")+"<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			for(String user:ulist.keySet()){
				nowTime = nowTime+ulist.get(user);
				long working = GetTime.getTimeByString(nowTime);
				long nowWork = System.currentTimeMillis();
				int times = (int) ((working-nowWork)/1000) ;
				System.out.println("Join to WorkingTimeThread:nowTime"+nowTime+" working=="+working+" nowWork=="+nowWork+" times:"+times+" user="+user);
				new WorkingNortiry(user,times).start();
				nowTime = GetTime.ConvertTimeByLong().substring(0,11);
			}
		}
	}
}
