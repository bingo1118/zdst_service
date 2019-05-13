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
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	个推cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	个推别名
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	个推ios绑定
			String appIdstr = this.request.getParameter("appId");
			String ifregister=this.request.getParameter("ifregister");//@@20180315技威是否注册 0否 1是
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);		//add by lzz at 2017-5-19 识别app
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

				//登陆成功后信息保存
				if(le.getErrorCode() == 0){
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 用于绑定ios入库
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//保存入库
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						pwd = MD5.encrypt(pwd+le.getSalt());
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 用于保存密码
					}
				}
				result = le;
			}else{
				hr = new HttpRsult();
				hr.setError("用户名不能为空");
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
	 * 最新登录成功获取随机toKen值
	 * 由随机5位数转成MD5加密形式
	 */
	public void userLogin(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	个推cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	个推别名
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	个推ios绑定
			String appIdstr = this.request.getParameter("appId");
			String ifregister=this.request.getParameter("ifregister");//@@20180315技威是否注册 0否 1是
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);		//add by lzz at 2017-5-19 识别app
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
					hr.setError("没有此用户");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 用于绑定ios入库
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//保存入库
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						pwd = MD5.encrypt(pwd+le.getSalt());
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 用于保存密码
					}
				}
			}else{
				hr = new HttpRsult();
				hr.setError("参数错误");
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
	
	//海曼登陆
	public void HMlogin(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String userId = this.request.getParameter("userId");
			String pwd = this.request.getParameter("pwd");
			String bindCid = this.request.getParameter("cid");	//add by lzz at 2017-5-16	个推cid
			//String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	个推别名
			String ios = this.request.getParameter("ios");		//add by lzz at 2017-5-18	个推ios绑定
			String ifregister="0";//@@20180315技威是否注册 0否 1是
			int appId = 5;//海曼
			
			
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
					hr.setError("没有此用户");
					hr.setErrorCode(2);
					result = hr;
				}else{
					if(Utils.isNullStr(ios)){			//add by lzo at 2017-5-18 用于绑定ios入库
						BindUserIdIosDao bindUserIdIosDao = new BindUserIdIosDaoImpl();
						bindUserIdIosDao.bindUserIdIos(userId, ios);
					}
					loginDao.updateUserAppId(userId, appId);		//add by lzz at 2017-5-19
					loginDao.savePushBindCid(userId, bindCid, 1);	//保存入库
					result = le;
					if(Utils.isNullStr(pwd)&&((LoginEntity)result).getErrorCode()==0){
						pwd = MD5.encrypt(pwd+le.getSalt());
						loginDao.loginToYooSee(userId, pwd); //add by lzz at 2017-5-15 用于保存密码
					}
				}
			}else{
				hr = new HttpRsult();
				hr.setError("参数错误");
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
					hr.setError("添加成功");
					hr.setErrorCode(0);
					result = hr;
				}else{
					hr = new HttpRsult();
					hr.setError("添加失败");
					hr.setErrorCode(2);
					result = hr;
				}
			}else{
				hr = new HttpRsult();
				hr.setError("参数错误");
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
					hrHttpRsult.setError("添加成功");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				else if(ret==3){
					hrHttpRsult.setError("用户已存在");
					hrHttpRsult.setErrorCode(3);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("添加失败");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("参数错误");
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
					hrHttpRsult.setError("密码错误");
					hrHttpRsult.setErrorCode(4);
					result=hrHttpRsult;
				}
				else if (ret==3){
					hrHttpRsult.setError("用户名不存在");
					hrHttpRsult.setErrorCode(3);
					result=hrHttpRsult;
				}
				else{
					hrHttpRsult.setError("登录成功");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("参数错误");
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
					hrHttpRsult.setError("发送成功");
					hrHttpRsult.setErrorCode(0);
					result=hrHttpRsult;
				}
				
				else{
					hrHttpRsult.setError("发送失败");
					hrHttpRsult.setErrorCode(2);
					result=hrHttpRsult;
				}
			}
			else{
				hrHttpRsult.setError("参数错误");
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
					infoString="验证成功";
				}
				
				else{
					infoString="验证失败";
				}
			}
			else{
				infoString="服务器异常";
			}		
			this.response.getWriter().write(infoString);
		} catch (Exception e) {
	
			e.printStackTrace();
		}		
	}
	
	
	/**
	 * 根据用户登录，获取随机toKen，并存储在 
	 */
}
