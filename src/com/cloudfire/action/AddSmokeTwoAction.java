package com.cloudfire.action;

import java.util.List;
import java.util.Timer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.impl.AddSmokeDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.RePeaterData;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.TimerMap;
import com.cloudfire.until.ClientPackage;
import com.cloudfire.until.LzstoneTimeTask;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class AddSmokeTwoAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private final static Log log = LogFactory.getLog(AddSmokeTwoAction.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AddSmokeDao mAddSmokeDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void addSmokeTwo(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String privilege = this.request.getParameter("privilege");
			String smokeName = this.request.getParameter("smokeName");
			String address = this.request.getParameter("address");
			String longitude = this.request.getParameter("longitude");
			String latitude = this.request.getParameter("latitude");
			String placeAddress = this.request.getParameter("placeAddress");
			String placeTypeId = this.request.getParameter("placeTypeId");
			String principal1 = this.request.getParameter("principal1");
			String principal1Phone = this.request.getParameter("principal1Phone");
			String principal2 = this.request.getParameter("principal2");
			String principal2Phone = this.request.getParameter("principal2Phone");
			String areaId = this.request.getParameter("areaId");
			String repeater = this.request.getParameter("repeater");
			String camera = this.request.getParameter("camera");
			String deviceType = this.request.getParameter("deviceType");
			
			String positions = this.request.getParameter("positions");
			String floors = this.request.getParameter("floors");
			String storeys = this.request.getParameter("storeys");
			HttpRsult hr = null;
			Object result = null;
			if(userId==null||smokeMac==null){
				hr = new HttpRsult();
				hr.setError("参数错误");
				hr.setErrorCode(1);
				result = hr;
			}else{
				mAddSmokeDao = new AddSmokeDaoImpl();
				hr = mAddSmokeDao.addSmokeTwo(userId, smokeMac, privilege, smokeName, address, longitude, latitude,
						placeAddress, placeTypeId, principal1, principal1Phone, principal2, principal2Phone, 
						areaId, repeater, camera, deviceType, positions, floors, storeys);
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("添加烟感失败");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = hr;
				}
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
			if(Utils.isNullStr(repeater)&&!deviceType.equals("9")&&hr.getErrorCode()==0){
				RePeaterData mRePeaterData = new RePeaterData();
				mRePeaterData.setRepeatMac(repeater);
				mRePeaterData.setSeqL((byte)0x01);
				mRePeaterData.setSeqH((byte)0x01);
				//查找全部烟感
				mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
				List<String> listStr = mGetSmokeMacByRepeaterDao.getSmokeMacByRepeater(repeater);
				int count=listStr.size()*4+2;
				byte[] ack = ClientPackage.synchronousFire(mRePeaterData,listStr,count);
				IoSession session = SessionMap.newInstance().getSession(repeater);
				if(session!=null){
					Timer timer = new Timer();     
		            timer.schedule(new LzstoneTimeTask(ack,session),0,1*5000); 
		            TimerMap.newInstance().addTimer(repeater, timer);
				}else{
					log.debug("区域管理终端不在线----------session=" + session);
				}
			}
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
