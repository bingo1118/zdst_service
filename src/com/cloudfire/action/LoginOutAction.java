package com.cloudfire.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONObject;

import com.cloudfire.dao.BindUserIdIosDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.BindUserIdIosDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.push.GeTuiConfiguration;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class LoginOutAction extends ActionSupport implements 
	ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = 2881084182258367796L;
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

	public void loginOut(){
		this.response.setContentType("text/html;charset=utf-8");
		this.response.setCharacterEncoding("GBK");
		try {
			this.request.setCharacterEncoding("utf-8");
			String alias = this.request.getParameter("alias");	//add by lzz at 2017-5-16	个推别名
			String cid = this.request.getParameter("cid");
			String userId = this.request.getParameter("userId");
			String appIdstr = this.request.getParameter("appId");
			int appId = 0;
			if(Utils.isNullStr(appIdstr)){
				appId = Integer.parseInt(appIdstr);
			}
			HttpRsult hr = null;
			Object result = null;
			loginDao = new LoginDaoImpl();
			
			appIdstr = GeTuiConfiguration.appId_zdst;
			
			boolean flag = loginDao.unBindAlias(appIdstr, alias,cid);
			boolean flag_ios = loginDao.unBindIOS(alias);
			
			if(flag){
				hr = new HttpRsult();
				hr.setError("退出解绑成功");
				hr.setErrorCode(0);
				result = hr;
			}else{
				hr = new HttpRsult();
				hr.setError("退出解绑失败");
				hr.setErrorCode(2);
				result = hr;
			}
			
			JSONObject jObject = new JSONObject(result);
			this.response.getWriter().write(jObject.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
