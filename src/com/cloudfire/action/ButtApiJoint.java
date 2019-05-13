package com.cloudfire.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;


import com.cloudfire.dao.AddSmokeDao;
import com.cloudfire.dao.AllAlarmDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.dao.ElectricTypeInfoDao;
import com.cloudfire.dao.GetSmokeMacByRepeaterDao;
import com.cloudfire.dao.KeepSystemDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.NeedSmokeDao;
import com.cloudfire.dao.OneElectricInfoDao;
import com.cloudfire.dao.PlaceTypeDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.SmokeLineDao;
import com.cloudfire.dao.impl.AddSmokeDaoImpl;
import com.cloudfire.dao.impl.AllAlarmDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.BindUserIdIosDaoImpl;
import com.cloudfire.dao.impl.ElectricTypeInfoDaoImpl;
import com.cloudfire.dao.impl.GetSmokeMacByRepeaterDaoImpl;
import com.cloudfire.dao.impl.KeepSystemDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.NeedSmokeDaoImpl;
import com.cloudfire.dao.impl.OneElectricInfoDaoImpl;
import com.cloudfire.dao.impl.PlaceTypeDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.db.RedisConnection;
import com.cloudfire.db.RedisOps;
import com.cloudfire.entity.AllAlarmEntity;
import com.cloudfire.entity.AllAreaEntity;
import com.cloudfire.entity.AllSmokeEntity;
import com.cloudfire.entity.ElectricInfo;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.OneElectricEntity;
import com.cloudfire.entity.PlaceTypeEntity;
import com.cloudfire.entity.Repeater;
//import com.cloudfire.entity.SmokeMap;
import com.cloudfire.thread.HengXingMQTT;
import com.cloudfire.until.StringUtil;
import com.cloudfire.until.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionSupport;

public class ButtApiJoint extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{

	private static final long serialVersionUID = -4581514309685063419L;
	private final static Log log = LogFactory.getLog(ButtApiJoint.class);
	private HttpServletRequest request;
	private HttpServletResponse response;
	private AddSmokeDao mAddSmokeDao;
//	private SmokeMap mSmokeMap;	
	private SmokeLineDao mSmokeLineDao;
	private GetSmokeMacByRepeaterDao mGetSmokeMacByRepeaterDao;
	private LoginDao loginDao;
	private AreaDao mAreaDao;
	private PlaceTypeDao mPlaceTypeDao;
	private ElectricTypeInfoDao mElectricTypeInfoDao;
	private OneElectricInfoDao mOneElectricInfoDao;
	private NeedSmokeDao mNeedSmokeDao;
	private AllAlarmDao mAllAlarmDao;
	
	HttpRsult hr = null;
	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	//1����¼API�ӿ�
	public void loggingApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	����cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	���Ʊ���
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	����ios��
			String appIdstr = this.request.getParameter("appId");
			String ifregister=this.request.getParameter("ifregister");//@@20180315�����Ƿ�ע�� 0�� 1��
			ifregister = "0";
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);		//add by lzz at 2017-5-19 ʶ��app
			}
			
			LoginEntity le = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId!=null&&userId.length()>0){
				loginDao = new LoginDaoImpl();
				if(Utils.isNullStr(ifregister)&&ifregister.equals("0")){
					le = loginDao.login2(userId,pwd);
				}
				if(le==null){
					hr = new HttpRsult();
					hr.setError("û�д��û�");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 ���ڰ�ios���
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//�������
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 ���ڱ�������
					}
				}
			}else{
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//2��ˢ��toKen�ӿ�
		public void refreshToken(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String pwd = this.request.getParameter("pwd");
				String ifregister=this.request.getParameter("ifregister");//@@20180315�����Ƿ�ע�� 0�� 1��
				ifregister = "0";
				LoginEntity le = null;
				HttpRsult hr = null;
				Object result = null;
				if(userId!=null&&userId.length()>0){
					loginDao = new LoginDaoImpl();
					if(Utils.isNullStr(ifregister)&&ifregister.equals("0")){
						le = loginDao.login2(userId,pwd);
					}
					if(le==null){
						hr = new HttpRsult();
						hr.setError("û�д��û�");
						hr.setErrorCode(2);
						result = hr;
					}else{
						result = le;
					}
				}else{
					hr = new HttpRsult();
					hr.setError("��������");
					hr.setErrorCode(1);
					result = hr;
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		public static void main(String[] args) {
			System.out.println(Utils.userMd5.get("15273535106"));
		}
	//3����ȡ����ӿ�
		public void getAreaIdApiJoint(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				String page = this.request.getParameter("page");
				String valToKen  = this.request.getParameter("valToKen");
				String comToKen = Utils.userMd5.get(userId);
				privilege = "3";
//				AreaIdEntity aet = null;
				AllAreaEntity ase = null;
				HttpRsult hr = null;
				Object result = null;
				if(userId==null||privilege==null||StringUtil.strIsNullOrEmpty(valToKen)){
					hr = new HttpRsult();
					hr.setError("��������");
					hr.setErrorCode(1);
					result = hr;
				}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
					hr = new HttpRsult();
					hr.setError("Token��֤ʧ��");
					hr.setErrorCode(3);
					result = hr;
				}else{
					mAreaDao = new AreaDaoImpl();
//					aet = mAreaDao.getAreaByUserId(userId, privilege, page);
					ase = mAreaDao.getAllAreas(userId, privilege);
					if(ase!=null){
						ase.setError("��ȡ����id�ɹ�");
						ase.setErrorCode(0);
						result = ase;
					}else{
						hr = new HttpRsult();
						hr.setError("��ȡ����idʧ��");
						hr.setErrorCode(2);
						result = hr;
					}
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		
		//4����ȡ��ҵ����
		public void getPlaceIdApiJoint(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				String page = this.request.getParameter("page");
				String valToKen  = this.request.getParameter("valToKen");
				String comToKen = Utils.userMd5.get(userId);
				privilege = "3";
				PlaceTypeEntity pte = null;
				HttpRsult hr = null;
				Object result = null;
				if(userId==null||StringUtils.isBlank(valToKen)){
					hr = new HttpRsult();
					hr.setError("��������");
					hr.setErrorCode(1);
					result = hr;
				}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
					hr = new HttpRsult();
					hr.setError("Token��֤ʧ��");
					hr.setErrorCode(3);
					result = hr;
				}else {
					mPlaceTypeDao = new PlaceTypeDaoImpl();
					pte = mPlaceTypeDao.getAllShopType(page);
					if(pte==null){
						hr = new HttpRsult();
						hr.setError("��ȡ��������ʧ��");
						hr.setErrorCode(2);
						result = hr;
					}else{
						pte.setError("��ȡ�������ͳɹ�");
						pte.setErrorCode(0);
						result = pte;
					}
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//5����ȡ�豸����
		public void getNeedDevApiJoint() {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String userId = this.request.getParameter("userId");
				String privilege = this.request.getParameter("privilege");
				String page  = this.request.getParameter("page");
				String areaId = this.request.getParameter("areaId");
				String placeTypeId  = this.request.getParameter("placeTypeId");
				String devType = this.request.getParameter("devType");//@@
				String parentId = this.request.getParameter("parentId");//@@������
				String valToKen  = this.request.getParameter("valToKen");
				privilege = "3";
				AllSmokeEntity ase = null;
				HttpRsult hr = null;
				Object result = null;
				
				String comToKen = Utils.userMd5.get(userId);
				if(userId==null||privilege==null||StringUtil.strIsNullOrEmpty(valToKen)){
					hr = new HttpRsult();
					hr.setError("��������");
					hr.setErrorCode(1);
					result = hr;
				}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
					hr = new HttpRsult();
					hr.setError("Token��֤ʧ��");
					hr.setErrorCode(3);
					result = hr;
				}else {
					mNeedSmokeDao = new NeedSmokeDaoImpl();
					if(privilege.equals("1")){//@@9.29 1��Ȩ�޲�ѯ
						ase = mNeedSmokeDao.getAdminAllSmoke(userId, privilege,page,devType);
					}else{
						ase = mNeedSmokeDao.getNeedDev(userId, privilege, page, areaId, placeTypeId,devType,parentId);
					}
					if(ase==null){
						hr = new HttpRsult();
						hr.setError("û������");
						hr.setErrorCode(2);
						result = hr;
					}else{
						ase.setError("��ȡ�ɹ�");
						ase.setErrorCode(0);
						result = ase;
					}
				}
				JSONObject jObject = new JSONObject(result);
				this.response.getWriter().write(jObject.toString());
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	
	//6�����API�ӿ�
	public void insertSmokeApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		Object result = null;
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
			String cameraChannel=this.request.getParameter("cameraChannel");
			String electrState=this.request.getParameter("electrState");//��������״̬
			String valToKen  = this.request.getParameter("valToKen");
			String comToKen = Utils.userMd5.get(userId);
			privilege = "3";
			if(electrState==null||electrState.equals("")){
				electrState="0";
			}
			
			KeepSystemDao keepSystemDao=new  KeepSystemDaoImpl();
			String oldRepeaterString=keepSystemDao.getRepeaterOfSmoke(smokeMac);
			if(userId==null||smokeMac==null||StringUtil.strIsNullOrEmpty(valToKen)){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else{
				mAddSmokeDao = new AddSmokeDaoImpl();
				hr = mAddSmokeDao.addSmoke(userId, smokeMac, privilege, smokeName, address, longitude, latitude,
						placeAddress, placeTypeId, principal1, principal1Phone, principal2, principal2Phone, 
						areaId, repeater, camera, deviceType,cameraChannel,electrState,"");
				if(hr==null){
					hr = new HttpRsult();
					hr.setError("����̸�ʧ��");
					hr.setErrorCode(2);
					result = hr;
				}else{
					mGetSmokeMacByRepeaterDao = new GetSmokeMacByRepeaterDaoImpl();
					if(Utils.isNullStr(repeater)&&!deviceType.equals("9")&&hr.getErrorCode()==0){
						Utils.sendRepeaterList(repeater);
					}
					if(Utils.isNullStr(repeater)&&!repeater.equals(oldRepeaterString)){
						Utils.sendRepeaterList(oldRepeaterString);
					}
					//��������豸redis�������б�
					if(StringUtils.isNotBlank(repeater)){
						Utils.updateOffSmokeList( repeater, deviceType, smokeMac, oldRepeaterString);
					}
//					if(!deviceType.equals("5")){ //�ǵ����豸����Ӳ�Ҫ���ڴ��е�smokeMap����  edit by yfs @12/8/2017 9:16
//						SmokeMap.newInstance().updateSmokeMap(repeater, smokeMac);
//						
//					}
					result = hr;
				}
			}
			if(deviceType.equals("69")||deviceType.equals("70")){
				HengXingMQTT.subScription("ND/"+smokeMac+"/sys_para",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/storage_data",1);
				HengXingMQTT.subScription("ND/"+smokeMac+"/alarm",1);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			result = hr;
			JSONObject jObject = new JSONObject(result);
			try {
				this.response.getWriter().write(jObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//7����ȡˮѹˮλ����
	public void getWaterApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String page = this.request.getParameter("page");
			String valToKen  = this.request.getParameter("valToKen");
			privilege = "3";
			String comToKen = Utils.userMd5.get(userId);
			
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			
			if(StringUtil.strIsNullOrEmpty(valToKen)||!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else {
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				aeie = mElectricTypeInfoDao.getWaterHistoryInfo(userId, privilege, smokeMac, page);
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("û��������Ϣ");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = aeie;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//8����ȡ���������豸����
	public void getOneElectricApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			//userId=13622215085&privilege=&smokeMac
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String smokeMac = this.request.getParameter("smokeMac");
			String privilege = this.request.getParameter("privilege");
			String valToKen  = this.request.getParameter("valToKen");
			
			String comToKen = Utils.userMd5.get(userId);
			privilege = "3";
			
			HttpRsult hr = null;
			Object result = null;
			OneElectricEntity oee = null;
			
			if(StringUtil.strIsNullOrEmpty(valToKen)||!Utils.isNullStr(userId)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(privilege)){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else {
				mOneElectricInfoDao = new OneElectricInfoDaoImpl();
				oee = mOneElectricInfoDao.getOneElectricInfo(smokeMac);
				if(oee==null){
					hr = new HttpRsult();
					hr.setError("û������");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = oee;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//9����ȡ�����豸���Խӿ�
	public void getElectricApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			//userId=13622215085&privilege=&smokeMac=59F8250A&electricType=6&electricNum=&page=1
			String userId = this.request.getParameter("userId");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String electricType = this.request.getParameter("electricType");
			String electricNum = this.request.getParameter("electricNum");
			String electricDate = this.request.getParameter("electricDate");
			String page = this.request.getParameter("page");
			String valToKen  = this.request.getParameter("valToKen");
			
			String comToKen = Utils.userMd5.get(userId);
			privilege = "3";
			HttpRsult hr = null;
			Object result = null;
			ElectricInfo aeie = null;
			
			if(StringUtil.strIsNullOrEmpty(valToKen)||!Utils.isNullStr(userId)||!Utils.isNullStr(privilege)||!Utils.isNullStr(smokeMac)||!Utils.isNullStr(electricType)||!Utils.isNullStr(electricNum)){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else {
				mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
				if(!Utils.isNullStr(electricDate)){
					aeie = mElectricTypeInfoDao.getElectricTypeInfo(userId, privilege, smokeMac, electricType, electricNum, page);
				}else{
					aeie = mElectricTypeInfoDao.getElectricPCTypeInfo(userId, privilege, 
							smokeMac, electricType, electricNum, page, electricDate);
				}
				
				if(aeie==null){
					hr = new HttpRsult();
					hr.setError("û�е���������Ϣ");
					hr.setErrorCode(2);
					result = hr;
				}else{
					result = aeie;
				}
			}
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//10����ȡ�������ݽӿ�
	public void getAlarmApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String page = this.request.getParameter("page");
			String privilege = this.request.getParameter("privilege");
			String smokeMac = this.request.getParameter("smokeMac");
			String valToKen  = this.request.getParameter("valToKen");
			
			String comToKen = Utils.userMd5.get(userId);
			privilege = "3";
			HttpRsult hr = null;
			Object result = null;
			AllAlarmEntity aae = null;
			if(StringUtil.strIsNullOrEmpty(valToKen)||userId==null||privilege==null){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else {
				mAllAlarmDao = new AllAlarmDaoImpl();
				if(privilege.equals("1")){
					aae = mAllAlarmDao.getNormalAllAlarmMsg(userId, privilege, page,smokeMac);
				}else{
					aae = mAllAlarmDao.getAdminAllAlarmMsg(userId, privilege, page,smokeMac);
				}
				
				if(aae==null){
					hr = new HttpRsult();
					hr.setError("û�б�����Ϣ");
					hr.setErrorCode(2);
					result = hr;
				}else{
					aae.setError("��ȡ������Ϣ�ɹ�");
					aae.setErrorCode(0);
					result = aae;
				}
			}
//			JSONObject jObject = new JSONObject(result);
//			this.response.getWriter().write(jObject.toString());
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//11����ȡ��ʪ���豸��ϸ����
		public void getTHDevApiJoint(){
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("GBK");
			try {
				this.request.setCharacterEncoding("utf-8");
				String smokeMac = this.request.getParameter("mac");
				String page = this.request.getParameter("page");
				String type=this.request.getParameter("type");//1�������¶ȡ�2������ʪ��
				String userId = this.request.getParameter("userId");
				String valToKen  = this.request.getParameter("valToKen");
				
				String comToKen = Utils.userMd5.get(userId);
				HttpRsult hr = null;
				Object result = null;
				ElectricInfo aeie = null;
				
				if(StringUtil.strIsNullOrEmpty(valToKen)||userId==null){
					hr = new HttpRsult();
					hr.setError("��������");
					hr.setErrorCode(1);
					result = hr;
				}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
					hr = new HttpRsult();
					hr.setError("Token��֤ʧ��");
					hr.setErrorCode(3);
					result = hr;
				}else {
					mElectricTypeInfoDao = new ElectricTypeInfoDaoImpl();
					aeie = mElectricTypeInfoDao.getTHDevInfoHistoryInfo(smokeMac, page,type);
					if(aeie==null){
						hr = new HttpRsult();
						hr.setError("û��������Ϣ");
						hr.setErrorCode(2);
						result = hr;
					}else{
						result = aeie;
					}
				}
				
				ObjectMapper mapper = new ObjectMapper();  
		        String json = mapper.writeValueAsString(result); 
		        this.response.getWriter().write(json);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	//12�����̸к����⹦������
	public void setSoundSmokeApiJoint(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String smokeMac = this.request.getParameter("smokeMac");
			String soundMac = this.request.getParameter("soundMac");
			String userId = this.request.getParameter("userId");
			String valToKen  = this.request.getParameter("valToKen");
			String repeaterMac = this.request.getParameter("repeaterMac");
			
			String comToKen = Utils.userMd5.get(userId);
			HttpRsult hr = null;
			Object result = null;
			
			if(StringUtil.strIsNullOrEmpty(valToKen)||userId==null||StringUtils.isBlank(smokeMac)||StringUtils.isBlank(soundMac)){
				hr = new HttpRsult();
				hr.setError("��������");
				hr.setErrorCode(1);
				result = hr;
			}else if(!valToKen.equals(comToKen)&&!valToKen.equals("HRSST")){
				hr = new HttpRsult();
				hr.setError("Token��֤ʧ��");
				hr.setErrorCode(3);
				result = hr;
			}else {
				PublicUtils pubdao= new PublicUtilsImpl();
				int ssr = pubdao.bindSmokeSoundRepeater(repeaterMac, soundMac, smokeMac);
				if(ssr>0){
					hr = new HttpRsult();
					hr.setError("���óɹ�");
					hr.setErrorCode(0);
					result = hr;
				}else{
					hr = new HttpRsult();
					hr.setError("����ʧ��");
					hr.setErrorCode(2);
					result = hr;
				}
			}
			
			ObjectMapper mapper = new ObjectMapper();  
	        String json = mapper.writeValueAsString(result); 
	        this.response.getWriter().write(json);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
