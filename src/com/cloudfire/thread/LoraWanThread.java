package com.cloudfire.thread;

import java.util.TimerTask;

import com.cloudfire.dao.LoraWanDao;
import com.cloudfire.dao.impl.LoraWanDaoImpl;

public class LoraWanThread extends TimerTask {
	private LoraWanDao lorawanDao;
	@Override
	public void run() {
		//�ж�lorawan�̸е��߻��ƣ�ÿ4��Сʱ�����ж�һ�Ρ�
		lorawanDao = new LoraWanDaoImpl();
		
	}
}
