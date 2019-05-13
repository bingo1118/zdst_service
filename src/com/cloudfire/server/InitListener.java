package com.cloudfire.server;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;

import com.cloudfire.dao.APIDao;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.APIDaoImpl;
import com.cloudfire.dao.impl.SmokeLineDaoImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.Repeater;
import com.cloudfire.entity.StisEntity;
import com.cloudfire.thread.CheckExpire;
import com.cloudfire.thread.GpsTimeThread;
import com.cloudfire.thread.HengXingMQTT;
import com.cloudfire.thread.HengXingMQTT148;
import com.cloudfire.thread.HengxingFalanWaterThread;
import com.cloudfire.thread.MQTTMemo;
import com.cloudfire.thread.RepeaterTimeTask;
import com.cloudfire.thread.SanJiangMQTTM;
import com.cloudfire.thread.getWXTokenTask;

public class InitListener implements ServletContextListener{
	private SmokeLineDao mSmokeLineDao;
	
	private ApplicationContext context; 
	
	public void contextDestroyed(ServletContextEvent arg0) {
		NioSocketAcceptor nioacceptor = (NioSocketAcceptor) context.getBean("ioAcceptor");  
	    nioacceptor.dispose();  
	}
		
	public void contextInitialized(ServletContextEvent sce) {
		context = new ClassPathXmlApplicationContext("applicationContext_minaServer.xml");
		//��tomcat������������,�ῴ������̨��ӡ�����.
		System.out.println("********mina server �������*********");
		
		try{
			//��ʼ������״̬�б���������豸�����б�
			mSmokeLineDao = new SmokeLineDaoImpl();
			Jedis jedis = RedisConnection.getJedis();
			Set<String> repeaterMacs = RedisOps.getRepeaters(jedis);
			if (repeaterMacs.isEmpty()) {
				 List<Repeater> list  = mSmokeLineDao.getAllRepeaters();
				 Iterator<Repeater> iterator = list.iterator();
				 while (iterator.hasNext()) {
					 Repeater rep = iterator.next();
					 //��ʼ������״̬����
					 RedisOps.setObject(jedis,"R"+rep.getRepeaterMac(),rep);
//					 //��ʼ�����������б�
					 RedisOps.setList(jedis,rep.getRepeaterMac()	,SmokeLineDaoImpl.getMacs(rep.getRepeaterMac()));
				 }
			}
			
			jedis.close();
		}catch(Exception e){
			e.printStackTrace();
		} 
		
        Timer timer4 = new Timer();
        timer4.schedule(new GpsTimeThread(), 2601,31001);	//add by lzo at 7-31
        
	}       
		
	
}
