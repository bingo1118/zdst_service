package com.cloudfire.action;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.PrinterDao;
import com.cloudfire.dao.impl.PrinterDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.PrinterAlarmResult;
import com.cloudfire.entity.PrinterInfoEntity;
import com.cloudfire.server.MyIoHandler;
import com.cloudfire.until.IntegerTo16;
import com.cloudfire.until.LogHelper;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class EquipmentOfOneRepeaterAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private final static Log log = LogFactory.getLog(MyIoHandler.class);
	private static final long serialVersionUID = 5848251641335538710L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private PrinterDao mPrinterDao;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void getEquipmentOfOneRepeater(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String repeater = this.request.getParameter("repeater");
			String page = this.request.getParameter("page");
			
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNumeric(repeater)&&!Utils.isNumeric(userId)&&!Utils.isNullStr(page)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(2);
				result = hr;
			}else{
				 mPrinterDao = new PrinterDaoImpl();
				 int repeaterType = mPrinterDao.getRepeaterType(repeater);
				 PrinterInfoEntity mPrinterInfoEntity = new PrinterInfoEntity();
				 System.out.println("repeaterType="+repeaterType);
				 if(repeaterType==126||repeaterType==119){//126代表设备类型为海湾主机
					 mPrinterInfoEntity=mPrinterDao.getEquipmentOfOneRepeater2(repeater,page);
				 }else{
					 mPrinterInfoEntity=mPrinterDao.getEquipmentOfOneRepeater(repeater,page);
				 }
				 if(mPrinterInfoEntity!=null){
					 result = mPrinterInfoEntity;
				 }else{
					 hr = new HttpRsult();
					 if(Integer.parseInt(page)==1){
						 hr.setError("没有数据");
						 hr.setErrorCode(1);
					 }else{
						 hr.setError("没有更多数据");
						 hr.setErrorCode(7);
					 }
					 result = hr;
				 }
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void getAlarmOfRepeater(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String repeater = this.request.getParameter("repeater");
			String smokeMac = this.request.getParameter("smokeMac");
			String faultDesc = this.request.getParameter("faultDesc");
			String startTime = this.request.getParameter("startTime");
			String endTime = this.request.getParameter("endTime");
			String page = this.request.getParameter("page");
			
			LogHelper.d("bingo::"+faultDesc);
			HttpRsult hr = null;
			Object result = null;
			if(!Utils.isNumeric(repeater)&&!Utils.isNumeric(userId)&&!Utils.isNullStr(page)){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(2);
				result = hr;
			}else{
				mPrinterDao = new PrinterDaoImpl();
				PrinterAlarmResult mPrinterAlarmResult = mPrinterDao.getAlarmOfRepeater(repeater, startTime, endTime,smokeMac,page,faultDesc);
				if(mPrinterAlarmResult!=null){
					result = mPrinterAlarmResult;
				}else{
					hr = new HttpRsult();
					if(Integer.parseInt(page) == 1){
						hr.setError("没有数据");
						hr.setErrorCode(1);
					}else{
						hr.setError("没有更多数据");
						hr.setErrorCode(7);
					}
					result = hr;
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
