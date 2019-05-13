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
        response.setContentType("image/jpeg");//������Ӧ����,������������������ΪͼƬ  
        response.setHeader("Pragma", "No-cache");//������Ӧͷ��Ϣ�������������Ҫ���������  
        response.setHeader("Cache-Control", "no-cache"); 
        response.setDateHeader("Expire", 0); 
        RandomValidateCode randomValidateCode = new RandomValidateCode(); 
        try { 
            randomValidateCode.getRandcode(request, response);//�����֤��ͼƬ����  
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
	
	  /**
     * ��¼ҳ��У����֤��
	 * @throws IOException 
     */
    @RequestMapping(value = "/checkVerify.do", method = RequestMethod.GET)
    public void checkVerify(String Code, String UserName,String PWD,String privId,HttpSession session,HttpServletResponse response,HttpServletRequest request) throws IOException{
    	mLoginDao = new LoginDaoImpl();
    	LoginHttpRsult hr = mLoginDao.verify(UserName, PWD, privId);
    	if (hr.getErrorCode() == 0){ //����½�ɹ����򱣴��û�Id��session
    		String ipaddress = Utils.getIpAddress(request);
    		mLoginDao.updateLoginState(UserName, 1, ipaddress);// �����½��Ϣ
    		
    		//���û���Ϣ����session������
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
    	
    	//��ȡ��½���˻���Ϣ
//    	UserLongDao userdao=new UserLongerDaoImpl();
//		UserEntity userEntity = userdao.getUserInfoByUserId(UserName);
//		LoginHttpRsult hr = new LoginHttpRsult();
//		if(userEntity== null) {
//			hr.setError("���û�������");
//			hr.setErrorCode(1);
//		}else{
//			int loginState = 0;
//			
//			 //�ж�Ȩ��
//			int privilege = userEntity.getPrivilege();
//			if(privilege==6||privilege==7) privilege = 3;
//			if(privilege!=3&&privilege!=4){
//	        	hr.setError("Ȩ�޲���,�����Ա��Ȩ");
//	        	hr.setErrorCode(1);
//			}
//			
//			if (hr.getErrorCode() == 0) { //Ȩ������
//		    	if(privId == null){  //��̨��½
//		    		if ("gzhr".equals(UserName)){
//		    			hr.setError("���ǹ���Ա,�������Ա��ϵ");
//		            	hr.setErrorCode(1);
//		    		}
//		    	} else { //ǰ̨��½
//		    		//���ж��û���ѡ�����Ƿ��뱾�������ͬ
//		    		int oripriv = userEntity.getPrivId();
//		    		if (privId.equals("1") && oripriv == 1) {  //������λ���û���½
//						CompanyDao cd = new CompanyDaoImpl();
//						if (cd.ifExitCompany(UserName)) {
//							loginState = 1;
//						} else {
//							loginState = 2;
//						} 
//						
//					} else if (privId.equals("3")&& oripriv == 3) { //��ܲ��ŵ��û���½
//						loginState = 3;
//					} else {
//			        	hr.setError( "��ѡ����û����Ͳ�ƥ�䣡");
//			        	hr.setErrorCode(1);
//					}
//		    	}
//	    	
//		    	if(hr.getErrorCode() != 1) {  //��Ȩ�����㣬������㣬����֤�û���������
//		    		hr = loginYoo(UserName,PWD);  //��֤�û���������
//		        	if (hr.getErrorCode() == 0){ //����½�ɹ����򱣴��û�Id��session
//		        		hr.setLoginState(loginState);
//		        		String ipaddress = Utils.getIpAddress(request);
//		        		mLoginDao.updateLoginState(UserName, 1, ipaddress);// �����½��Ϣ
//		        		
//		        		//���û���Ϣ����session������
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
	    	
	    	//����½���д���Ӧ
	    	Object obj = JSON.toJSON(hr);
			response.getWriter().write(obj.toString());
		}
		
    	
        //��session�л�ȡ�����
//        String random = (String) session.getAttribute("RANDOMVALIDATECODEKEY");
//        session.setAttribute("privId", privId);
//        session.setAttribute("UserName", UserName);
//        try {//{"SessionID2":"","SessionID":"345316900","ImageID":"","Email":"","UserLevel":"0",
        	//"NickName":"","UserID":"-2142185056","error_code":"0",
        	//"CountryCode":"86","P2PVerifyCode2":"2147004791","DomainList":" ",
        	//"PhoneNO":"13622215085","P2PVerifyCode1":"2065587111"}
        	
//        	LoginHttpRsult hr = null;
//        	
//        	//��½Ȩ��ֻ���� 3 4 6 7,������ʾȨ�޲���
//    		if(userEntity!=null){ //���û��Ѿ�ע��
//        		int privilege =userEntity.getPrivilege();
//        		if(privilege==6||privilege==7) privilege = 3;
//        		if(privilege!=3&&privilege!=4){
//	        		hr = new LoginHttpRsult();
//		        	hr.setError("Ȩ�޲���,�����Ա��Ȩ");
//		        	hr.setErrorCode(1);
//        		}else{
//        			count--;
//        		}
//    		}
//    		
////        	if(!Utils.isNullStr(privId)){  //��̨��½
////        		UserLongDao userdao=new UserLongerDaoImpl();
////        		UserEntity userEntity = userdao.getUserInfoByUserId(UserName);
////        		if(userEntity!=null){ //���û��Ѿ�ע��
////	        		int privilege =userEntity.getPrivilege();
////	        		if(privilege==6||privilege==7) privilege = 3;
////	        		if(privilege!=3&&privilege!=4){
////		        		hr = new LoginHttpRsult();
////			        	hr.setError("Ȩ�޲���");
////			        	hr.setErrorCode(1);
////	        		}else{
////	        			count--;
////	        		}
////        		}
////        	}
//        	if(hr==null){  //Ȩ�޹��ˣ���ȥ��֤����
//	        	hr = loginYoo(UserName,PWD);  //��֤�û���������
//	        	if (hr.getErrorCode() == 0){ //����½�ɹ����򱣴��û�Id��session
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
    	if(mLoginDao.getSaltByUserId(UserName) == null || mLoginDao.getSaltByUserId(UserName).equals("")) {//��Ϊ�գ���������֮ǰδ���ܵ�
    		i = mLoginDao.loginUser(UserName, PWD);
    	}else{//����Ϊ֮����ܼ��ε�
    		String soft = mLoginDao.getSaltByUserId(UserName);
    		String password = MD5.encrypt(PWD + soft);
    		i = mLoginDao.loginUser(UserName, password);
    	}
		if(i==0){   //�û������벻�������ݿ⣬������������½��֤
			//�û����Ƿ�������ݿ�
			LoginEntity mLoginEntity = mLoginDao.login(UserName);
			
			JSONObject mJSONObject =LoginYooSee.login(UserName, PWD);
	    	LoginResult result = LoginYooSee.createLoginResult(mJSONObject);
	    	int resultCode =Integer.parseInt(result.error_code);
	    	
	    	//goeasy��userId����
//	    	int loginState = mLoginDao.getLoginState(UserName);
//	    	if(loginState==1&&mLoginEntity.getPrivilege()!=4) {
//	    		Utils.sendMessage(UserName, "["+"13622215085"+"] ���㷢��:["+4+"]");
//	    	}
	    	
	    	switch (resultCode) {
			case 0:
	        	if(mLoginEntity.getErrorCode()==0){
	        		hr.setError("��¼�ɹ�");
		        	hr.setErrorCode(0);
		        	hr.setUserId(UserName);
		        	mLoginDao.loginToYooSee(UserName, PWD);	//add by lzz 2017-5-17�����û�����������ݿ�
//		        	hr.setPrivId(privId);
	        	}else{
	        		hr.setError("���û��������ݿ���,�뵽��̨���");
		        	hr.setErrorCode(1);
	        	}
				break;
			case 3:
				hr.setError("�û��������");
	        	hr.setErrorCode(1);
				break;
			case 4:
				hr.setError("���û��ѱ���¼��");
				hr.setErrorCode(1);
				break;
//			case 998:
//				loginYoo(UserName,PWD);
//				break;
			case 2:
				hr.setError("�û�������");
	        	hr.setErrorCode(1);
			default:
				hr.setError("��¼ʧ��");
				hr.setErrorCode(1);
				break;
			}
		}else{
			hr.setError("��¼�ɹ�");
        	hr.setErrorCode(0);
        	hr.setUserId(UserName);
		}
    	
    	return hr;
    }
    
    //��΢��
    @RequestMapping(value = "/bindWX.do", method = RequestMethod.GET)
    public ModelAndView  bindWX(String code, String UserName,String PWD,HttpSession session,HttpServletResponse response,HttpServletRequest request){
    	ModelAndView mv = new ModelAndView();
    	//��ȡ��½���˻���Ϣ
		LoginHttpRsult hr = null;
		
		String open_id=getOpenId(code);
		String result = "1";
    	if(hr == null) {  //��Ȩ�����㣬������㣬����֤�û���������
    		hr = loginYoo(UserName,PWD);  //��֤�û���������
        	if (hr.getErrorCode() == 0){ //����½�ɹ����򱣴��û�Id��session
        		boolean bool = bindWXandAccount(UserName,open_id);
        		if(bool){
        			result = "0";
        		}
        	}
    	}
    	
    	//����½���д���Ӧ
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
    
  //���΢��
    @RequestMapping(value = "/unbindWX.do", method = RequestMethod.GET)
    public ModelAndView  unbindWX(String code, String UserName,String PWD,HttpSession session,HttpServletResponse response,HttpServletRequest request){
    	ModelAndView mv = new ModelAndView();
    	//��ȡ��½���˻���Ϣ
		LoginHttpRsult hr = null;
		
		String open_id=getOpenId(code);
		String result = "1";
    	if(hr == null) {  //��Ȩ�����㣬������㣬����֤�û���������
    		hr = loginYoo(UserName,PWD);  //��֤�û���������
        	if (hr.getErrorCode() == 0){ //����½�ɹ����򱣴��û�Id��session
        		boolean bool = unbindWXandAccount(UserName,open_id);
        		if(bool){
        			result = "0";
        		}
        	}
    	}
    	
    	
    	//����½���д���Ӧ
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
			
	        System.out.println(connect.getResponseCode()); // ��Ӧ���� 200��ʾ�ɹ�
	        if (connect.getResponseCode() == 200) {
	            inStream = connect.getInputStream();
	            String result = new String(ByteArrayUtils.toByteArray(inStream), "UTF-8");
	            JSONObject jsonObject=new JSONObject(result);
	            System.out.println("@@@@@@@@getresult:"+result); // ��Ӧ���� 200��ʾ�ɹ�
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
