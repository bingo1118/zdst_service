package com.cloudfire.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.dao.AddUserDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.AddUserDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.query.MyHttpResult;
import com.gexin.fastjson.JSON;

@Controller
public class AddUserController {
	private AddUserDao mAddUserDao;
	private LoginDao mLoginDao;
	private AreaDao mAreaDao;
	
	
	@RequestMapping(value = "/addUserResult.do",method = RequestMethod.POST)
	public void addUserResult(String username,String name,Integer[] addressed,String quanxian,HttpServletResponse response,HttpServletRequest request){
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("GBK");
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mAddUserDao = new AddUserDaoImpl();
		LoginDao mLoginDao = new LoginDaoImpl();
		LoginEntity entity = mLoginDao.login(username);
		String pwd = request.getParameter("pwd");
		String isCanCutEletr=request.getParameter("isCanCutEletr");
		if(StringUtils.isBlank(pwd)){
			pwd = "123456";
		}
		//boolean ifExit = mAddUserDao.ifExitUser(username);
		if(entity.getErrorCode() == 2){ //不存在该用户，插入数据库
			mAddUserDao.addUser(username, 3, name,pwd);
		}else if(entity.getPrivilege()!=1){ //存在该用户，且权限不为1，则其权限不变
			mAddUserDao.updateUser(username, entity.getPrivilege(), name,isCanCutEletr);
		}else{
			mAddUserDao.updateUser(username, 3, name,isCanCutEletr);
		}
		
		//修改用户绑定的区域列表
		mAddUserDao.deleteUserArea(username);
		mAddUserDao.addUserArea(username, addressed);
		
		HttpRsult hr = new HttpRsult();
		
		hr.setError("添加成功");
    	hr.setErrorCode(0);
    	
		Object obj = JSON.toJSON(hr);
        try {
			response.getWriter().write(obj.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//验证码正确
	}
	
	
	@RequestMapping(value="findUserByUserId.do")
	public void findUserByUserId(String username,HttpServletResponse response,HttpServletRequest request){
		MyHttpResult hr = new MyHttpResult();
		mLoginDao = new LoginDaoImpl();
		LoginEntity user = mLoginDao.login(username);
		if (user.getErrorCode() != 2) { //已经存在数据库的用户
			String privilege = user.getPrivilege()+"";
			request.getSession().setAttribute("privilege", privilege);
			if(user.getPrivilege()==1){
				privilege = "3";
			}
			mAreaDao = new AreaDaoImpl();
			List<String> areIds = mAreaDao.getAreaStr(username, privilege);
			hr.setUsername(user.getName());
			hr.setIsCanCutEletr(user.getIsCanCutEletr());
			hr.setList(areIds);
			hr.setError("该用户已注册");
			hr.setErrorCode(1);
		}else {
			hr.setError("该用户是新用户");
			hr.setErrorCode(0);
		}
	
		Object object = JSON.toJSON(hr);
		try {
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
