package com.cloudfire.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AllSmokeDao;
import com.cloudfire.dao.EnvironmentDao;
import com.cloudfire.dao.impl.AllSmokeDaoImpl;
import com.cloudfire.dao.impl.EnvironmentDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.EnvironmentEntity;
import com.cloudfire.entity.EnvironmentHistoryEntity;
import com.cloudfire.entity.EnvironmentHistoryMsgEntity;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class AllEnvironmentAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = 2633439111157020460L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private EnvironmentDao mEnvironmentDao;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}
	
	public void getEnvironmentHistory(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String airMac = this.request.getParameter("airMac");
			EnvironmentHistoryEntity environmentHistoryEntity = null;
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(airMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				String timeString="2017-08-24";
				String datesString=null;
				String datesString2=null;
				Date date=null;
				List<EnvironmentHistoryMsgEntity> environmentHistoryMsgEntityListCo2=new ArrayList<EnvironmentHistoryMsgEntity>();
				List<EnvironmentHistoryMsgEntity> environmentHistoryMsgEntityListPm25=new ArrayList<EnvironmentHistoryMsgEntity>();
				List<EnvironmentHistoryMsgEntity> environmentHistoryMsgEntityListHumidity=new ArrayList<EnvironmentHistoryMsgEntity>();
				List<EnvironmentHistoryMsgEntity> environmentHistoryMsgEntityListTemperature=new ArrayList<EnvironmentHistoryMsgEntity>();
				List<EnvironmentHistoryMsgEntity> environmentHistoryMsgEntityListmethanal=new ArrayList<EnvironmentHistoryMsgEntity>();
				EnvironmentDao environmentDao=new EnvironmentDaoImpl();
				try {
					date = sdf.parse(timeString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar nowCalendar=Calendar.getInstance();
				nowCalendar.setTime(new Date());
				for (int i=0;i<6;i++){
					nowCalendar.add(Calendar.DATE, -1);
					
					StringBuffer dateStringBuffer=new StringBuffer();
					StringBuffer dateStringBuffer2=new StringBuffer();
					dateStringBuffer.append(nowCalendar.get(Calendar.YEAR));
					dateStringBuffer.append("-");
					int month=nowCalendar.get(Calendar.MONTH)+1;
					if(month<10)
						dateStringBuffer.append("0");
					dateStringBuffer.append(month);
					dateStringBuffer.append("-");
					int day=nowCalendar.get(Calendar.DATE);
					if(day<10)
						dateStringBuffer.append("0");
					dateStringBuffer.append(day);
					
					dateStringBuffer2.append(nowCalendar.get(Calendar.YEAR));
					dateStringBuffer2.append("-");
					if(month<10)
						dateStringBuffer2.append("0");
					dateStringBuffer2.append(month);
					dateStringBuffer2.append("-");
				
					if(day<10)
						dateStringBuffer2.append("0");
					dateStringBuffer2.append(day+1);
					
					datesString=dateStringBuffer.toString();
					datesString2=dateStringBuffer2.toString();
					
					System.out.println("dateString :"+datesString);
					EnvironmentHistoryMsgEntity environmentHistoryMsgEntity=environmentDao.getHistoryData(airMac, datesString, datesString2, 1);
					if(environmentHistoryMsgEntity!=null){
						if(environmentHistoryEntity==null){
							environmentHistoryEntity=new EnvironmentHistoryEntity();					
						}
						environmentHistoryMsgEntityListCo2.add(environmentHistoryMsgEntity);				
					}
					
					environmentHistoryMsgEntity=null;
					environmentHistoryMsgEntity=environmentDao.getHistoryData(airMac, datesString, datesString2, 2);
					if(environmentHistoryMsgEntity!=null){
						if(environmentHistoryEntity==null){
							environmentHistoryEntity=new EnvironmentHistoryEntity();					
						}
						environmentHistoryMsgEntityListPm25.add(environmentHistoryMsgEntity);				
					}
					
					environmentHistoryMsgEntity=null;
					environmentHistoryMsgEntity=environmentDao.getHistoryData(airMac, datesString, datesString2, 3);
					if(environmentHistoryMsgEntity!=null){
						if(environmentHistoryEntity==null){
							environmentHistoryEntity=new EnvironmentHistoryEntity();					
						}
						environmentHistoryMsgEntityListmethanal.add(environmentHistoryMsgEntity);				
					}
					
					environmentHistoryMsgEntity=null;
					environmentHistoryMsgEntity=environmentDao.getHistoryData(airMac, datesString, datesString2, 4);
					if(environmentHistoryMsgEntity!=null){
						if(environmentHistoryEntity==null){
							environmentHistoryEntity=new EnvironmentHistoryEntity();					
						}
						environmentHistoryMsgEntityListHumidity.add(environmentHistoryMsgEntity);				
					}
					
					environmentHistoryMsgEntity=null;
					environmentHistoryMsgEntity=environmentDao.getHistoryData(airMac, datesString, datesString2, 5);
					if(environmentHistoryMsgEntity!=null){
						if(environmentHistoryEntity==null){
							environmentHistoryEntity=new EnvironmentHistoryEntity();					
						}
						environmentHistoryMsgEntityListTemperature.add(environmentHistoryMsgEntity);				
					}
				}
				if(environmentHistoryEntity!=null){
					environmentHistoryEntity.setCo2(environmentHistoryMsgEntityListCo2);
					
					environmentHistoryEntity.setPm25(environmentHistoryMsgEntityListPm25);
					
					environmentHistoryEntity.setMathanal(environmentHistoryMsgEntityListmethanal);
				
					environmentHistoryEntity.setHumidity(environmentHistoryMsgEntityListHumidity);
				
					environmentHistoryEntity.setTemperature(environmentHistoryMsgEntityListTemperature);
				}
				if(environmentHistoryEntity==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					environmentHistoryEntity.setError("获取环境探测器成功");
					environmentHistoryEntity.setErrorCode(0);
					result = environmentHistoryEntity;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getEnvironmentInfo(){

		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String airMac = this.request.getParameter("airMac");
			EnvironmentEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNullStr(airMac)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mEnvironmentDao = new EnvironmentDaoImpl();
				ase = mEnvironmentDao.getEnvironmentEntityInfo(airMac);
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有数据");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取环境探测器成功");
					ase.setErrorCode(0);
					result = ase;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getAllEnvironment() {
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String page  = this.request.getParameter("page");
			AllSmokeEntity ase = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mEnvironmentDao = new EnvironmentDaoImpl();
				if(privilege.equals("1")){
					ase = mEnvironmentDao.getNotSmokeMac(userId, privilege, page);
				}else{
					ase = mEnvironmentDao.getSuperNotSmokeMac(userId, privilege, page);
				}
				if(ase==null){
					hr = new HttpRsult();
					hr.setError("没有烟感");
					hr.setErrorCode(2);
					result = hr;
				}else{
					ase.setError("获取烟感成功");
					ase.setErrorCode(0);
					result = ase;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
