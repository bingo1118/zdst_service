package com.cloudfire.action;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.BindUserIdIosDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.until.MD5;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	
	private static final long serialVersionUID = 4716799081625424847L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private LoginDao loginDao;

	public void setServletResponse(HttpServletResponse response) {
		// TODO Auto-generated method stub
		this.response = response;
	}

	public void setServletRequest(HttpServletRequest request) {
		// TODO Auto-generated method stub
		this.request = request;
	}

	public void login(){
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
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);		//add by lzz at 2017-5-19 ʶ��app
			}
			
			
			LoginEntity le = null;
			HttpRsult hr = null;
			Object result = null;
			if(StringUtils.isNotBlank(userId)){
				loginDao = new LoginDaoImpl();
				if(Utils.isNullStr(ifregister)&&ifregister.equals("0")){
					le = loginDao.login2(userId,pwd);
				}else{
					le = loginDao.login(userId);
				}

				//��½�ɹ�����Ϣ����
				if(le.getErrorCode() == 0){
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 ���ڰ�ios���
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//�������
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						pwd = MD5.encrypt(pwd+le.getSalt());
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 ���ڱ�������
					}
				}
				result = le;
			}else{
				hr = new HttpRsult();
				hr.setError("�û�������Ϊ��");
				hr.setErrorCode(1);
				result = hr;
			}
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * ���µ�¼�ɹ���ȡ���toKenֵ
	 * �����5λ��ת��MD5������ʽ
	 */
	public void userLogin(){
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
				}else{
					le = loginDao.login(userId);
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
						pwd = MD5.encrypt(pwd+le.getSalt());
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
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//������½
	public void HMlogin(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	����cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	���Ʊ���
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	����ios��
			String ifregister="0";//@@20180315�����Ƿ�ע�� 0�� 1��
			int appId = 5;//����
			
			
			LoginEntity le = null;
			HttpRsult hr = null;
			Object result = null;
			if(userId!=null&&userId.length()>0){
				loginDao = new LoginDaoImpl();
				if(Utils.isNullStr(ifregister)&&ifregister.equals("0")){
					le = loginDao.login2(userId,pwd);
				}else{
					le = loginDao.login(userId);
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
						pwd = MD5.encrypt(pwd+le.getSalt());
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
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//@@ liangbin 2017.09.28
	public void register(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			
			
			HttpRsult hr = null;
			Object result = null;
			if(userId!=null&&userId.length()>0){
				loginDao = new LoginDaoImpl();
				boolean le=loginDao.addUser(userId,1, pwd);
				if(le){
					hr = new HttpRsult();
					hr.setError("��ӳɹ�");
					hr.setErrorCode(0);
					result = hr;
				}else{
					hr = new HttpRsult();
					hr.setError("���ʧ��");
					hr.setErrorCode(2);
					result = hr;
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
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//by liao zhenwei 2017 11 2 
	public  void elecMeterRegister(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  user=this.request.getParameter("user");
			String password=this.request.getParameter("password");
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(user!=null && user.length()>0){
				loginDao=new LoginDaoImpl();
				int ret=loginDao.elecMeterAddUser(user, password);
				if(ret==0){
					hrHttpRsult.setError("��ӳɹ�");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				else if(ret==3){
					hrHttpRsult.setError("�û��Ѵ���");
					hrHttpRsult.setErrorCode(3);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("���ʧ��");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("��������");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}
		
	}
	
	public  void elecMeterLogin(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  user=this.request.getParameter("user");
			String password=this.request.getParameter("password");
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(user!=null && user.length()>0){
				loginDao=new LoginDaoImpl();
				int ret=loginDao.elecMeterLogin(user, password);
				if(ret==4){
					hrHttpRsult.setError("�������");
					hrHttpRsult.setErrorCode(4);
					result=hrHttpRsult;
				}
				else if (ret==3){
					hrHttpRsult.setError("�û���������");
					hrHttpRsult.setErrorCode(3);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("��¼�ɹ�");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("��������");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	
	public  void elecMeterSendEmail(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  email=this.request.getParameter("email");		
			HttpRsult hrHttpRsult=new HttpRsult();
			Object result=null;
			if(email!=null && email.length()>0){
				loginDao=new LoginDaoImpl();
				int ret=loginDao.elecMeterSendEmail(email);
				if(ret>0){
					hrHttpRsult.setError("���ͳɹ�");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				
				else{
					hrHttpRsult.setError("����ʧ��");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("��������");
				hrHttpRsult.setErrorCode(1);
				result=hrHttpRsult;
			}
			JSONObject jsonObject=new JSONObject(result);
			this.response.getWriter().write(jsonObject.toString());
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	public  void elecMeterCheckEmail(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("gbk");
		try {
			this.request.setCharacterEncoding("utf-8");
			String  serialCode=this.request.getParameter("serialCode");	
			String email=this.request.getParameter("email");
			String infoString="";
			if(serialCode!=null && serialCode.length()>0){
				loginDao=new LoginDaoImpl();
				int ret=loginDao.elecMeterCheckEmail(serialCode, email);
				if(ret>0){						
					infoString="��֤�ɹ�";
				}
				
				else{
					infoString="��֤ʧ��";
				}
			}
			else{
				infoString="�������쳣";
			}		
			this.response.getWriter().write(infoString);
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * �����û���¼����ȡ���toKen�����洢�� 
	 */
}
