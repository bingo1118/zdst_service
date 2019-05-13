package com.cloudfire.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.CompanyDao;
import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.UserLongDao;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.CompanyDaoImpl;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.cloudfire.dao.impl.UserLongerDaoImpl;
import com.cloudfire.db.DBConnectionManager;
import com.cloudfire.entity.AreaAndRepeater;
import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.LoginHttpRsult;
import com.cloudfire.entity.LoginResult;
import com.cloudfire.entity.UserEntity;
import com.cloudfire.until.ByteArrayUtils;
import com.cloudfire.until.Constant;
import com.cloudfire.until.LoginYooSee;
import com.cloudfire.until.MD5;
import com.cloudfire.until.RandomValidateCode;
import com.cloudfire.until.Utils;
import com.gexin.fastjson.JSON;



@Controller
public class LoginInWebController {
	private LoginDao mLoginDao;
	
	@RequestMapping(value = "/welcome.do", method = RequestMethod.GET)
	public ModelAndView homeWeb(){
		ModelAndView mModelAndView= new ModelAndView();
		mModelAndView.setViewName("/welcome");
		return mModelAndView;
	}
	
	@RequestMapping(value = "/getVerify.do")
    public void getVerify(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片  
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容  
        response.setHeader("Cache-Control", "no-cache"); 
        response.setDateHeader("Expire", 0); 
        RandomValidateCode randomValidateCode = new RandomValidateCode(); 
        try { 
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法  
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
	
	  /**
     * 登录页面校验验证码
	 * @throws IOException 
     */
    @RequestMapping(value = "/checkVerify.do", method = RequestMethod.GET)
    public void checkVerify(String Code, String UserName,String PWD,String privId,HttpSession session,HttpServletResponse response,HttpServletRequest request) throws IOException{
    	mLoginDao = new LoginDaoImpl();
    	LoginHttpRsult hr = mLoginDao.verify(UserName, PWD, privId);
    	if (hr.getErrorCode() == 0){ //若登陆成功，则保存用户Id到session
    		String ipaddress = Utils.getIpAddress(request);
    		mLoginDao.updateLoginState(UserName, 1, ipaddress);// 保存登陆信息
    		
    		//将用户信息存入session作用域
    		session.setAttribute("userId", UserName);
    		session.setAttribute("privilege",hr.getPrivId());
    		session.setAttribute("appkey", Constant.appk_web_sub);
    		AreaDao ad = new AreaDaoImpl();
    		List<AreaAndRepeater> areas =  ad.getAllAreaByUserId(UserName, hr.getPrivId());
    		session.setAttribute("areas", areas);
    		List<String> areaIds = ad.getAreaStr(UserName, hr.getPrivId());
    		session.setAttribute("areaIds", areaIds);
    		String areaIdsStr = listToStr(areaIds);
    		session.setAttribute("areaIdsStr", areaIdsStr);
    	}
    	
    	//获取登陆的账户信息
//    	UserLongDao userdao=new UserLongerDaoImpl();
//		UserEntity userEntity = userdao.getUserInfoByUserId(UserName);
//		LoginHttpRsult hr = new LoginHttpRsult();
//		if(userEntity== null) {
//			hr.setError("此用户不存在");
//			hr.setErrorCode(1);
//		}else{
//			int loginState = 0;
//			
//			 //判断权限
//			int privilege = userEntity.getPrivilege();
//			if(privilege==6||privilege==7) privilege = 3;
//			if(privilege!=3&&privilege!=4){
//	        	hr.setError("权限不足,请管理员授权");
//	        	hr.setErrorCode(1);
//			}
//			
//			if (hr.getErrorCode() == 0) { //权限满足
//		    	if(privId == null){  //后台登陆
//		    		if ("gzhr".equals(UserName)){
//		    			hr.setError("不是管理员,请与管理员联系");
//		            	hr.setErrorCode(1);
//		    		}
//		    	} else { //前台登陆
//		    		//再判断用户所选机构是否与本身机构相同
//		    		int oripriv = userEntity.getPrivId();
//		    		if (privId.equals("1") && oripriv == 1) {  //建筑单位的用户登陆
//						CompanyDao cd = new CompanyDaoImpl();
//						if (cd.ifExitCompany(UserName)) {
//							loginState = 1;
//						} else {
//							loginState = 2;
//						} 
//						
//					} else if (privId.equals("3")&& oripriv == 3) { //监管部门的用户登陆
//						loginState = 3;
//					} else {
//			        	hr.setError( "您选择的用户类型不匹配！");
//			        	hr.setErrorCode(1);
//					}
//		    	}
//	    	
//		    	if(hr.getErrorCode() != 1) {  //若权限满足，身份满足，再验证用户名和密码
//		    		hr = loginYoo(UserName,PWD);  //验证用户名，密码
//		        	if (hr.getErrorCode() == 0){ //若登陆成功，则保存用户Id到session
//		        		hr.setLoginState(loginState);
//		        		String ipaddress = Utils.getIpAddress(request);
//		        		mLoginDao.updateLoginState(UserName, 1, ipaddress);// 保存登陆信息
//		        		
//		        		//将用户信息存入session作用域
//		        		session.setAttribute("userId", UserName);
//		        		session.setAttribute("privilege",privilege+"");
//		        		session.setAttribute("appkey", Constant.appk_web_sub);
//		        		AreaDao ad = new AreaDaoImpl();
//		        		List<AreaAndRepeater> areas =  ad.getWaterAreaByUserId(UserName, privilege+"");
//		        		session.setAttribute("areas", areas);
//		        		List<String> areaIds = ad.getAreaStr(UserName, privilege+"");
//		        		session.setAttribute("areaIds", areaIds);
//		        		String areaIdsStr = listToStr(areaIds);
//		        		session.setAttribute("areaIdsStr", areaIdsStr);
//		        	}
//		    	}
//	    	}
	    	
	    	//将登陆结果写入回应
	    	Object obj = JSON.toJSON(hr);
			response.getWriter().write(obj.toString());
		}
		
    	
        //从session中获取随机数
//        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
//        session.setAttribute("privId", privId);
//        session.setAttribute("UserName", UserName);
//        try {//{"SessionID2":"","SessionID":"345316900","ImageID":"","Email":"","UserLevel":"0",
        	//"NickName":"","UserID":"-2142185056","error_code":"0",
        	//"CountryCode":"86","P2PVerifyCode2":"2147004791","DomainList":" ",
        	//"PhoneNO":"13622215085","P2PVerifyCode1":"2065587111"}
        	
//        	LoginHttpRsult hr = null;
//        	
//        	//登陆权限只允许 3 4 6 7,否则提示权限不足
//    		if(userEntity!=null){ //该用户已经注册
//        		int privilege =userEntity.getPrivilege();
//        		if(privilege==6||privilege==7) privilege = 3;
//        		if(privilege!=3&&privilege!=4){
//	        		hr = new LoginHttpRsult();
//		        	hr.setError("权限不足,请管理员授权");
//		        	hr.setErrorCode(1);
//        		}else{
//        			count--;
//        		}
//    		}
//    		
////        	if(!Utils.isNullStr(privId)){  //后台登陆
////        		UserLongDao userdao=new UserLongerDaoImpl();
////        		UserEntity userEntity = userdao.getUserInfoByUserId(UserName);
////        		if(userEntity!=null){ //该用户已经注册
////	        		int privilege =userEntity.getPrivilege();
////	        		if(privilege==6||privilege==7) privilege = 3;
////	        		if(privilege!=3&&privilege!=4){
////		        		hr = new LoginHttpRsult();
////			        	hr.setError("权限不足");
////			        	hr.setErrorCode(1);
////	        		}else{
////	        			count--;
////	        		}
////        		}
////        	}
//        	if(hr==null){  //权限够了，再去验证密码
//	        	hr = loginYoo(UserName,PWD);  //验证用户名，密码
//	        	if (hr.getErrorCode() == 0){ //若登陆成功，则保存用户Id到session
//	        		session.setAttribute("userId", UserName);
//	        		session.setAttribute("appkey", Constant.appk_web_sub);
//	        	}
//	        }
//  
//        	
//	        Object obj = JSON.toJSON(hr);
//	        response.getWriter().write(obj.toString());
//        } catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    } 
    
    private String listToStr(List<String> areaIds) {
    	StringBuffer sb = new StringBuffer();
		if(areaIds !=null && areaIds.size() > 0) {
			sb.append("(");
			int size = areaIds.size();
			for(int i=0;i<size - 1; i++){
				sb.append(areaIds.get(i)+",");
			}
			sb.append(areaIds.get(size - 1)+")");
		}
		return sb.toString();
	}

	private LoginHttpRsult loginYoo(String UserName,String PWD){
    	LoginHttpRsult hr = new LoginHttpRsult();
    	mLoginDao = new LoginDaoImpl();
    	int i;
    	if(mLoginDao.getSaltByUserId(UserName) == null || mLoginDao.getSaltByUserId(UserName).equals("")) {//盐为空，则密码是之前未加密的
    		i = mLoginDao.loginUser(UserName, PWD);
    	}else{//否则为之后加密加盐的
    		String soft = mLoginDao.getSaltByUserId(UserName);
    		String password = MD5.encrypt(PWD + soft);
    		i = mLoginDao.loginUser(UserName, password);
    	}
		if(i==0){   //用户名密码不存在数据库，第三方技威登陆验证
			//用户名是否存在数据库
			LoginEntity mLoginEntity = mLoginDao.login(UserName);
			
			JSONObject mJSONObject =LoginYooSee.login(UserName, PWD);
	    	LoginResult result = LoginYooSee.createLoginResult(mJSONObject);
	    	int resultCode =Integer.parseInt(result.error_code);
	    	
	    	//goeasy按userId发布
//	    	int loginState = mLoginDao.getLoginState(UserName);
//	    	if(loginState==1&&mLoginEntity.getPrivilege()!=4) {
//	    		Utils.sendMessage(UserName, "["+"13622215085"+"] 向你发送:["+4+"]");
//	    	}
	    	
	    	switch (resultCode) {
			case 0:
	        	if(mLoginEntity.getErrorCode()==0){
	        		hr.setError("登录成功");
		        	hr.setErrorCode(0);
		        	hr.setUserId(UserName);
		        	mLoginDao.loginToYooSee(UserName, PWD);	//add by lzz 2017-5-17，将用户密码存入数据库
//		        	hr.setPrivId(privId);
	        	}else{
	        		hr.setError("该用户不在数据库中,请到后台添加");
		        	hr.setErrorCode(1);
	        	}
				break;
			case 3:
				hr.setError("用户密码错误");
	        	hr.setErrorCode(1);
				break;
			case 4:
				hr.setError("该用户已被登录。");
				hr.setErrorCode(1);
				break;
//			case 998:
//				loginYoo(UserName,PWD);
//				break;
			case 2:
				hr.setError("用户不存在");
	        	hr.setErrorCode(1);
			default:
				hr.setError("登录失败");
				hr.setErrorCode(1);
				break;
			}
		}else{
			hr.setError("登录成功");
        	hr.setErrorCode(0);
        	hr.setUserId(UserName);
		}
    	
    	return hr;
    }
    
    //绑定微信
    @RequestMapping(value = "/bindWX.do", method = RequestMethod.GET)
    public ModelAndView  bindWX(String code, String UserName,String PWD,HttpSession session,HttpServletResponse response,HttpServletRequest request){
    	ModelAndView mv = new ModelAndView();
    	//获取登陆的账户信息
		LoginHttpRsult hr = null;
		
		String open_id=getOpenId(code);
		String result = "1";
    	if(hr == null) {  //若权限满足，身份满足，再验证用户名和密码
    		hr = loginYoo(UserName,PWD);  //验证用户名，密码
        	if (hr.getErrorCode() == 0){ //若登陆成功，则保存用户Id到session
        		boolean bool = bindWXandAccount(UserName,open_id);
        		if(bool){
        			result = "0";
        		}
        	}
    	}
    	
    	//将登陆结果写入回应
    	/*Object obj = JSON.toJSON(hr);
        try {
			response.getWriter().write(obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        request.getSession().setAttribute("results", result);
    	
        mv.setViewName("/Wechat_result");
        return mv;
    	
    }
    
  //解绑微信
    @RequestMapping(value = "/unbindWX.do", method = RequestMethod.GET)
    public ModelAndView  unbindWX(String code, String UserName,String PWD,HttpSession session,HttpServletResponse response,HttpServletRequest request){
    	ModelAndView mv = new ModelAndView();
    	//获取登陆的账户信息
		LoginHttpRsult hr = null;
		
		String open_id=getOpenId(code);
		String result = "1";
    	if(hr == null) {  //若权限满足，身份满足，再验证用户名和密码
    		hr = loginYoo(UserName,PWD);  //验证用户名，密码
        	if (hr.getErrorCode() == 0){ //若登陆成功，则保存用户Id到session
        		boolean bool = unbindWXandAccount(UserName,open_id);
        		if(bool){
        			result = "0";
        		}
        	}
    	}
    	
    	
    	//将登陆结果写入回应
    	/*Object obj = JSON.toJSON(hr);
        try {
			response.getWriter().write(obj.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
        request.getSession().setAttribute("results", result);
    	
        mv.setViewName("/Wechat_result");
        return mv;
    	
    }

	private boolean bindWXandAccount(String userName, String open_id) {
		String strsql = "INSERT INTO userid_openid (userid, openid) VALUES (?, ?) ON DUPLICATE KEY UPDATE openid=?";

		int ids = -1;
		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
		try {
			ps.setString(1,userName );
			ps.setString(2, open_id);
			ps.setString(3, open_id);
			ids = ps.executeUpdate();
			if (ids > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			// DBConnectionManager.close(rs);
			DBConnectionManager.close(conn);
		}
		return false;
		
	}
	
	private boolean unbindWXandAccount(String userName, String open_id) {
		String strsql = "delete from userid_openid where userid=?";

		Connection conn = DBConnectionManager.getConnection();
		PreparedStatement ps = DBConnectionManager.prepare(conn, strsql);
		
		int ids = -1;
		try {
			ps.setString(1,userName);
			
			ids = ps.executeUpdate();
			if (ids > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBConnectionManager.close(ps);
			// DBConnectionManager.close(rs);
			DBConnectionManager.close(conn);
		}
		return false;
	}
	
	

	private  String getOpenId(String code) {
		InputStream inStream=null;
		try {
			String urlstring=" https://api.weixin.qq.com/sns/oauth2/access_token?appid=wx314fd3193dea2861&secret=c375ba2acf7dc848a9b79879fe926b86&code="+code+"&grant_type=authorization_code";
			URL url=new URL(urlstring);
			HttpURLConnection connect=(HttpURLConnection)url.openConnection();
			connect.setRequestMethod("GET");
			connect.connect();
			
	        System.out.println(connect.getResponseCode()); // 响应代码 200表示成功
	        if (connect.getResponseCode() == 200) {
	            inStream = connect.getInputStream();
	            String result = new String(ByteArrayUtils.toByteArray(inStream), "UTF-8");
	            JSONObject jsonObject=new JSONObject(result);
	            System.out.println("@@@@@@@@getresult:"+result); // 响应代码 200表示成功
	            return jsonObject.getString("openid");
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			if(inStream!=null){
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return null;
	} 
}
