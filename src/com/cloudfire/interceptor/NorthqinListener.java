package com.cloudfire.interceptor;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import antlr.debug.Event;

/**
 * 处理与北擎的数据对接的监听器，每隔半个小时执行一次
 * @author daill
 * */
public class NorthqinListener implements ServletContextListener{
	private Timer timer=null;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		timer.cancel();
		System.out.println("-----------与北擎数据对接的监听器销毁--------------");
		
	}

	
	 /** 
	   *  监听器初始化 
	   *  java.util.Timer.schedule(TimerTask task, long delay, long period) 
	   *  调用0表示任务无延迟 
	   *  5*1000表示每隔5秒执行任务 
	   *  30*60*1000表示半个小时 
	   *  24*60*60*1000表示一天。 
	   */ 
	@Override
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		timer = new Timer();
		new NorthqinTask(event.getServletContext()).run();
		System.out.println("-------定时器已经启动，已经执行一次---------");
		timer.schedule(new NorthqinTask(event.getServletContext()), 0, 30*60*1000);
		
	}

}
