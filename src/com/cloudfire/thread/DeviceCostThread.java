package com.cloudfire.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.SmartControlDao;
import com.cloudfire.dao.impl.SmartControlDaoImpl;
import com.cloudfire.entity.DeviceCostEntity;

public class DeviceCostThread extends TimerTask{ 
	private final static Log log = LogFactory.getLog(DeviceCostThread.class);
	private SmartControlDao smartControlDao;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void run() {
		smartControlDao=new SmartControlDaoImpl();
		
				//查找出所有费用正常的设备即cost>0的设备
				List<DeviceCostEntity> list=smartControlDao.getAllSmoke();
				List<String> list2=new ArrayList<String>();
				int rs=0;
				for (int i = 0; i < list.size(); i++) {
					String mac=list.get(i).getRepeaterMac();
					Double cost=list.get(i).getCost();
					Double finallyCost=cost-0.05;
					//更新数据库中设备的费用余额
					rs+=smartControlDao.updateCostByMac(mac,finallyCost);
					if(finallyCost<=0){
						list2.add(mac);
					}
					log.debug("成功更新了"+rs+"条数据");
					
				}
				try {
					request.setAttribute("list2", list2);
					request.getRequestDispatcher("showCostAlarm.do").forward(request, response);
				} catch (ServletException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}
}
