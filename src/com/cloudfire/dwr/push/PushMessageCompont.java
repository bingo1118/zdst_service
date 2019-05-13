package com.cloudfire.dwr.push;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.directwebremoting.spring.SpringCreator;

import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.ToolDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.ToolDaoImpl;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AreaBean;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.SessionMap;
import com.cloudfire.entity.SmokeXYZ;
import com.cloudfire.thread.AckElecContrThread;
import com.cloudfire.until.Client_Fault_Package;
import com.cloudfire.until.Constant;
import com.cloudfire.until.Utils;
import com.cloudfire.until.WriteJson;


@RemoteProxy(name="pushMessageCompont",creator=SpringCreator.class)
public class PushMessageCompont {
	private AreaDao mAreaDao;
	
	@RemoteMethod
	public String onPageLoad(final String userId) {
		ScriptSession scriptSession = WebContextFactory.get().getScriptSession();
		scriptSession.setAttribute(PushMessageUtil.DEFAULT_MARK, userId);
		String appkey = Constant.appk_web;
//		String appkey = Constant.appk_web_sub;
		return appkey;
	}
	
	@RemoteMethod
	public List<String> getAreaIds(String userId){
		AreaDaoImpl areaDaoImpl = new AreaDaoImpl();
		
		List<String> areaIds=areaDaoImpl.getAreaIdsByUserId(userId);
		return areaIds;
	}
	
	@RemoteMethod
	public int getPrivilege(String userId){
		LoginDao mLoginDao = new LoginDaoImpl();
		LoginEntity mLoginEntity = mLoginDao.login(userId);
		int privilege = mLoginEntity.getPrivilege();
		return privilege;
	}
	
	@RemoteMethod
	public String getHrTel(String userid){
		int privilege = getPrivilege(userid);
		if(privilege==1||privilege==4){
			return userid;
		}else return Constant.hrsst_tel;
	}
	
	/**
	 * @param userid
	 * @return 根据用户返回绑定的摄像头
	 */
	@RemoteMethod
	public List<String> getCamera(String userid){
		List<String> cameraList = new ArrayList<String>();
		PublicUtils pdao = new PublicUtilsImpl();
		cameraList = pdao.selectCamera(userid);
		return cameraList;
	}
	
	/**
	 * @param userid
	 * @return 解除绑定的摄像头
	 */
	@RemoteMethod
	public List<String> delCamera(String userid,String devMac){
		PublicUtils pdao = new PublicUtilsImpl();
		int result = pdao.delCamera(userid,devMac);
		List<String> cameraList = new ArrayList<String>();
		if(result>0){
			cameraList = this.getCamera(userid);
		}
		return cameraList;
	}
	
	/**
	 * @param userid
	 * @return 根据用户返回绑定的摄像头
	 */
	@RemoteMethod
	public List<String> getRepeater(String userid){
		List<String> cameraList = new ArrayList<String>();
		PublicUtils pdao = new PublicUtilsImpl();
		cameraList = pdao.selectRepeater(userid);
		return cameraList;
	}
	
	/**
	 * @param userid
	 * @return 解除绑定的主机推送
	 */
	@RemoteMethod
	public List<String> delRepeater(String userid,String devMac){
		PublicUtils pdao = new PublicUtilsImpl();
		int result = pdao.delRepeater(userid,devMac);
		List<String> cameraList = new ArrayList<String>();
		if(result>0){
			cameraList = this.getRepeater(userid);
		}
		return cameraList;
	}
	
	/**
	 * @param userid
	 * @return 根据用户返回绑定的烟感
	 */
	@RemoteMethod
	public List<String> getSmoke(String userid){
		List<String> cameraList = new ArrayList<String>();
		PublicUtils pdao = new PublicUtilsImpl();
		cameraList = pdao.selectSmoke(userid);
		return cameraList;
	}
	
	
	
	/**
	 * @param userid
	 * @return 解除绑定的烟感
	 */
	@RemoteMethod
	public List<String> delSmoke(String userid,String devMac){
		PublicUtils pdao = new PublicUtilsImpl();
		int result = pdao.delSmoke(userid,devMac);
		List<String> cameraList = new ArrayList<String>();
		if(result>0){
			cameraList = this.getSmoke(userid);
		}
		return cameraList;
	}
	
	@RemoteMethod
	public String getComputerName(){
		String named = Utils.getCurrentRunningServerComputerName();
		return named;
	}
	
	@RemoteMethod
	public String getAreaIds(){
		mAreaDao = new AreaDaoImpl();
		Map<Integer,String> parentIds = mAreaDao.getParentAll();
		List<AreaBean> lists = mAreaDao.getAll();
		AllAreaEntity entity = new AllAreaEntity();
		entity.setAreaBean(lists);
		entity.setParentIds(parentIds);
		List<AllAreaEntity> elist =new ArrayList<AllAreaEntity>();
		elist.add(entity);
		WriteJson writeJson = new WriteJson();
        String jObject = writeJson.getJsonData(elist);
		return jObject;
	}
	
	@RemoteMethod
	public String getAreaIdsByUserId(String userid,String privilege){
		mAreaDao = new AreaDaoImpl();
		Map<Integer,String> parentIds = null;
		List<AreaBean> lists = null;
		if(Integer.parseInt(privilege)==4){
			parentIds = mAreaDao.getParentAll();
			lists = mAreaDao.getAll();
		}else{
			parentIds = mAreaDao.getParentAll(userid);
			lists = mAreaDao.getAll(userid);
		}
		AllAreaEntity entity = new AllAreaEntity();
		entity.setAreaBean(lists);
		entity.setParentIds(parentIds);
		List<AllAreaEntity> elist =new ArrayList<AllAreaEntity>();
		elist.add(entity);
		WriteJson writeJson = new WriteJson();
        String jObject = writeJson.getJsonData(elist);
		return jObject;
	}
	
	@RemoteMethod
	public int updateParent(int areaId,int parentId){
		AreaDao adao = new AreaDaoImpl();
		int result = adao.updatePareaIdAreaId(areaId, parentId);
		return result;
	}
	
	/*
	 * 声光消音
	 */
	@RemoteMethod
	public String cancelSound(String repeaterMac){
		String result = "";
		List<String> soundList = new ArrayList<String>();
		ToolDao td = new ToolDaoImpl();
		soundList = td.getSoundList(repeaterMac);
		byte[] ack = Client_Fault_Package.ackAlarmPackage(repeaterMac,(byte)0x05,soundList);
		IoSession session = SessionMap.newInstance().getSession(repeaterMac);
		if(session!=null){
			AckElecContrThread ackEle = new AckElecContrThread(ack,session,repeaterMac);
			ackEle.start();
			result = "声光消音命令下发成功";
		}else{
			result = "命令失效，请检查通信状态";
		}
		
		return result;
	}
	
	@RemoteMethod
	public String getCountXYZ(String userId) {
		String str = "";
		if(StringUtils.isBlank(userId)){
			userId = "13622215085";//当用户为空时，则定义所有数据。
		}
		List<SmokeXYZ> list = new ArrayList<SmokeXYZ>();
		LoginDao mLoginDao  = new LoginDaoImpl();
		AreaDao mAreaDao = new AreaDaoImpl();
		LoginEntity entity = mLoginDao.login(userId);
		String privilege = entity.getPrivilege()+"";
		List<String> areIds = mAreaDao.getAreaStr(userId, privilege);
		list = mAreaDao.getAllXYZ(areIds);
		if (list.size()>0 && list !=null) {
			WriteJson json = new WriteJson();
			String object = json.getJsonData(list);
			str = object;
		}
		return str;
	}
	
	@RemoteMethod
	public String ifExitUserName(String username){
		LoginDao ld = new LoginDaoImpl();
		String result = "";
		if(ld.charactUserName(username)){
			result = "0";
		}else{
			result = "1";
		}
		return result;
	}
	

	/**
	 * 从uname类中查找cdata变量的值
	 */
	@RemoteMethod
	public String getcdata(String uname,String cdata){
		String result = "";
		try {
			result = "com.cloudfire.until."+uname;
			Class<?> clazz = Class.forName(result);
			Field filed = clazz.getField(cdata);
			result = filed.get(clazz).toString();
		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
		}
		return result;
	}
}
