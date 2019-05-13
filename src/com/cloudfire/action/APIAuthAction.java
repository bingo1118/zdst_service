package com.cloudfire.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
//import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudfire.dao.APIDao;
import com.cloudfire.dao.SmokeDao;
import com.cloudfire.dao.impl.APIDaoImpl;
import com.cloudfire.dao.impl.SmokeDaoImpl;
import com.cloudfire.entity.APIResult;
import com.cloudfire.entity.AlarmEntityForMQ;
import com.cloudfire.entity.SmokeBean;
import com.cloudfire.entity.StisEntity;
import com.cloudfire.push.RBMQClient;
import com.cloudfire.until.GetTime;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.rsautil.RSAUtil;

public class APIAuthAction implements ServletRequestAware,ServletResponseAware{
	private static String pubKey;
	private static String privKey;
//	private static String userName="test";
//	private static String passWord="Use123fortest";
	public static String tokenId;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	private APIResult rs ;
	private static Map<String,String> keyMap;
//	private static long deadline; //tokenId的有效期
	
	static {
		 keyMap = RSAUtil.generateKeyBytes();
		 pubKey = keyMap.get("publicKey");
//			PublicKey publicKey = RSAUtil.restorePublicKey(keyMap.get("publicKey"));
			
		privKey = keyMap.get("privateKey");
//			PrivateKey privateKey =RSAUtil.restorePrivateKey(keyMap.get("privateKey"));
	}
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.resp=response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.req=request;
		try {
			req.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void getPublicKey(){
		rs = new APIResult();
		rs.setCode(0);
		rs.setMessage("获取成功");
		Map<String,String> data=new HashMap<String,String>();
		data.put("publicKey", pubKey);
		rs.setData(data);
		writeResult();
	}
	
	public void getPrivateKey(){
		rs = new APIResult();
		rs.setCode(0);
		rs.setMessage("获取成功");
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("privateKey", privKey);
		rs.setData(data);
		writeResult();
	}
	
	public void getEncodedPassword(){
		String usr = req.getParameter("userName");
		String pwd = APIDaoImpl.getPwd(usr);
		rs = new APIResult();
		rs.setCode(0);
		rs.setMessage("获取成功");
		
	
		PublicKey publicKey = RSAUtil.restorePublicKey(pubKey);
//		byte[] encodedText = RSAUtil.RSAEncode(publicKey, "Use123fortest".getBytes());
		byte[] encodedText = RSAUtil.RSAEncode(publicKey, pwd.getBytes());
		String encodedPassword =   Base64.encodeBase64String(encodedText);
		Map<String,Object> data=new HashMap<String,Object>();
		data.put("encodedPassword", encodedPassword);
		rs.setData(data);
		writeResult();
	}
	
	
	public void login(){
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		PrivateKey privateKey = RSAUtil.restorePrivateKey(privKey);
		String pwd = RSAUtil.RSADecode(privateKey, password);
        System.out.println("RSA decoded: " + pwd);
        
        rs = new APIResult();
        if(APIDaoImpl.existUser(username,pwd)) {
//        if (userName.equals(username)&&passWord.equals(pwd)){
        	tokenId = UUID.randomUUID().toString().replace("-", "");
        	APIDaoImpl.updateToken(username, tokenId);
        	rs.setCode(0);
        	rs.setMessage("获取成功");
        	Map<String,Object> data=new HashMap<String,Object>();
    		data.put("tokenId", tokenId);
    		rs.setData(data);
        } else {
        	rs.setCode(1);
        	rs.setMessage("密码错误");
        }
        
        writeResult();
	}
	
	
	private void writeResult(){
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(rs);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) throws Exception{

		
		String baseUrl = "http://localhost:8080/fireSystem";
		String token = "f527d3fe33a7467e89c464a23cd9ec5c";
		

		postBuilding(baseUrl,token);
		postCompany(baseUrl,token);
		postEquipment(baseUrl,token);
		postStatics(baseUrl,token);
		
		
	}
	
	public static String getEncodePasswordByGet(String baseUrl,String userName){
//		String baseUrl = "http://localhost:8080/fireSystem";
//		String baseUrl ="http://139.159.220.138:51091/fireSystem";
		String url = baseUrl+"/api/getEncodedPassword?userName="+userName;
		String result = OneNetHttpMethod.get(url);
		String encodedPassword = "";
		try {
			JSONObject rs = new JSONObject(result);
			if (rs.getInt("code") == 0) {
				JSONObject data = new JSONObject(rs.getString("data"));
				encodedPassword = data.getString("encodedPassword");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return encodedPassword;
	}
	
	
	public static void postLogin(String baseUrl,String userName){
//		String baseUrl = "http://localhost:8080/fireSystem";
//		String baseUrl ="http://139.159.220.138:51091/fireSystem";
		String encodedPassword = getEncodePasswordByGet(baseUrl,userName);
		if (StringUtils.isNotBlank(encodedPassword)){
			String url = baseUrl+"/api/login";
			Map<String,String> map = new HashMap<String,String>();
//			map.put("username", "test");
			map.put("username", userName);
	//		String pub =getMqPublicKey(baseUrl);
	//		PublicKey publicKey = RSAUtil.restorePublicKey(pub);
	//		byte[] encodedText = RSAUtil.RSAEncode(publicKey, passWord.getBytes());
	//		String encodedPassword =   Base64.encodeBase64String(encodedText);
	//		System.out.println("RSA encoded:"+encodedPassword.replace("\r\n","\\r\\n"));
	//		map.put("password", encodedPassword.replace("\r\n","\\r\\n"));
			map.put("password",encodedPassword);
			System.out.println(OneNetHttpMethod.postMapInForm(url, map));
		}
	}
	
	public static void postCompany(String baseUrl,String token){
//		String url = "http://localhost:8080/fireSystem/api/getCompany";
//		String url = "http://139.159.220.138:51091/fireSystem/api/getCompany";
		String url = baseUrl + "/api/getCompany";
		Map<String,String> map = new HashMap<String,String>();
		map.put("pageSize", "50");
		map.put("pageNo", "1");
		map.put("createDateB",""+ GetTime.getTimeByString("2018-01-18 00:00:00")/1000);
		map.put("createDateE",""+ GetTime.getTimeByString("2019-03-25 00:00:00")/1000);
//		map.put("createDateB", "");
//		map.put("createDateE","");
//		map.put("updateDateB", "");
//		map.put("updateDateE", "");
//		map.put("id", "2985");
//		String token = "4a66b7bb85ab4e9694a64952173c3ab3";
		System.out.println(OneNetHttpMethod.postMapInForm2(url, map,token));
	}
	
	public static void postBuilding(String baseUrl,String token){
//		String url = "http://localhost:8080/fireSystem/api/getBuilding";
//		String url = "http://139.159.220.138:51091/fireSystem/api/getBuilding";
		String url = baseUrl + "/api/getBuilding";
		Map<String,String> map = new HashMap<String,String>();
		map.put("pageSize", "100");
		map.put("pageNo", "1");
		map.put("createDateB",""+ GetTime.getTimeByString("2018-01-18 00:00:00")/1000);
		map.put("createDateE",""+ GetTime.getTimeByString("2019-03-25 00:00:00")/1000);
//		map.put("updateDateB", "");
//		map.put("updateDateE", "");
//		map.put("id", "1001");
//		String token = "aa1e21bdc57d4265bb402b85cafa5569";
		System.out.println(OneNetHttpMethod.postMapInForm2(url, map,token));
	}
	
	public static void postEquipment(String baseUrl,String token){
//		String url = "http://localhost:8080/fireSystem/api/getEquipment";
//		String url = "http://139.159.220.138:51091/fireSystem/api/getEquipment";
		String url = baseUrl + "/api/getEquipment";
		Map<String,String> map = new HashMap<String,String>();
		map.put("pageSize", "50");
		map.put("pageNo", "1");
		map.put("createDateB",""+ GetTime.getTimeByString("2018-01-18 00:00:00")/1000);
		map.put("createDateE",""+ GetTime.getTimeByString("2019-03-25 00:00:00")/1000);
//		map.put("createDateB", "");
//		map.put("createDateE","");
//		map.put("updateDateB", "");
//		map.put("updateDateE", "");
//		map.put("id", "00000077");
//		map.put("id", "0024E6F8");
//		String token = "aa1e21bdc57d4265bb402b85cafa5569";
		System.out.println(OneNetHttpMethod.postMapInForm2(url, map,token));
	}
	
	public static void postStatics(String baseUrl,String token){
//		String url = "http://localhost:8080/fireSystem/api/getStatistics";
//		String url = "http://139.159.220.138:51091/fireSystem/api/getStatistics";
		String url = baseUrl + "/api/getStatistics";
		Map<String,String> map = new HashMap<String,String>();
		map.put("pageSize", "50");
		map.put("pageNo", "1");
		map.put("statiDataB",""+ GetTime.getTimeByString("2018-01-18 00:00:00")/1000);
		map.put("statiDataE",""+ GetTime.getTimeByString("2019-03-25 00:00:00")/1000);
//		map.put("statiDataB", "1547740800000");
//		map.put("statiDataE","1548086400000");
//		String token = "aa1e21bdc57d4265bb402b85cafa5569";
		System.out.println(OneNetHttpMethod.postMapInForm2(url, map,token));
	}
	
	public static String getMqPublicKey(String baseUrl){
//		String url = "http://122.228.28.27:8190/api/getPublicKey";
		String url = baseUrl + "/api/getPublicKey";
		String result = OneNetHttpMethod.get(url);
		int  code = 0;
		String message = "";
		String publicKey ="";
		try {
			JSONObject json = new JSONObject(result);
			code = json.getInt("code");
			message =json.getString("message");
			if (code == 0){
				JSONObject data = new JSONObject(json.getString("data"));
				publicKey = data.getString("publicKey");
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		System.out.println("code:"+code);
		System.out.println("message:"+message);
		System.out.println("publicKey:"+publicKey);
		return publicKey;
	}
	
	public static void getMqInfo(String baseUrl){
//		String baseUrl = "http://122.228.28.27:8190"; 
		String key =getMqPublicKey(baseUrl);
		if (StringUtils.isNotBlank(key)){
			String username ="mqUser";
			String password="mq258456";
			PublicKey publicKey = RSAUtil.restorePublicKey(key);
			byte[] encodedText = RSAUtil.RSAEncode(publicKey, password.getBytes());
			String encodedPassword =   Base64.encodeBase64String(encodedText);
			String url = baseUrl+"/api/getMqInfo";
			Map<String,String> map = new HashMap<String,String>();
			map.put("username", username);
			map.put("password", encodedPassword);
			String result = OneNetHttpMethod.postMapInForm(url, map);
			System.out.println("result:"+result);
			try {
				JSONObject json = new JSONObject(result);
				int  code = json.getInt("code");
				String message =json.getString("message");
				if (code == 0){
					JSONObject data = new JSONObject(json.getString("data"));
					JSONObject mqInfo = new JSONObject(data.getString("mqInfo"));
					String username1 = mqInfo.getString("username");
					String password1 = mqInfo .getString("password");
					JSONObject queue = new JSONObject(mqInfo.getString("queue"));
					String remoteMonitoring = queue.getString("remoteMonitoring");
					String wisdomWater = queue.getString("wisdomWater");
					String wisdomElectricity = queue.getString("wisdomElectricity");
					String wisdomWarning = queue.getString("wisdomWarning");
					String chargingPile = queue.getString("chargingPile");
					String other = queue.getString("other");
					System.out.println("code:"+code);
					System.out.println("message:"+message);
					System.out.println("username1:"+username1);
					System.out.println("password1:"+password1);
					System.out.println("remoteMonitoring:"+remoteMonitoring);
					System.out.println("wisdomWater:"+wisdomWater);
					System.out.println("wisdomElectricity:"+wisdomElectricity);
					System.out.println("wisdomWarning:"+wisdomWarning);
					System.out.println("chargingPile:"+chargingPile);
					System.out.println("other:"+other);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
