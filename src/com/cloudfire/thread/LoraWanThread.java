package com.cloudfire.thread;

import java.util.TimerTask;

import com.cloudfire.dao.LoraWanDao;
import com.cloudfire.dao.impl.LoraWanDaoImpl;

public class LoraWanThread extends TimerTask {
	private LoraWanDao lorawanDao;
	@Override
	public void run() {
		//判断lorawan烟感掉线机制：每4个小时进行判断一次。
		lorawanDao = new LoraWanDaoImpl();
		
	}
}
