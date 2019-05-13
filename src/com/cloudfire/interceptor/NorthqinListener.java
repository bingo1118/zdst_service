package com.cloudfire.interceptor;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import antlr.debug.Event;

/**
 * �����뱱������ݶԽӵļ�������ÿ�����Сʱִ��һ��
 * @author daill
 * */
public class NorthqinListener implements ServletContextListener{
	private Timer timer=null;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		timer.cancel();
		System.out.println("-----------�뱱�����ݶԽӵļ���������--------------");
		
	}

	
	 /** 
	   *  ��������ʼ�� 
	   *  java.util.Timer.schedule(TimerTask task, long delay, long period) 
	   *  ����0��ʾ�������ӳ� 
	   *  5*1000��ʾÿ��5��ִ������ 
	   *  30*60*1000��ʾ���Сʱ 
	   *  24*60*60*1000��ʾһ�졣 
	   */ 
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		timer = new Timer();
		new NorthqinTask(event.getServletContext()).run();
		System.out.println("-------��ʱ���Ѿ��������Ѿ�ִ��һ��---------");
		timer.schedule(new NorthqinTask(event.getServletContext()), 0, 30*60*1000);
		
	}

}
