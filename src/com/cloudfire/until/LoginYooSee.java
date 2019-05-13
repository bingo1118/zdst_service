package com.cloudfire.until;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.cloudfire.entity.LoginResult;

public class LoginYooSee {
	private static String CURRENT_SERVER;
	private static String LOGIN_URL = CURRENT_SERVER + "Users/LoginCheck.ashx";
	private static String GETPHONE_CODE_URL = CURRENT_SERVER
			+ "Users/PhoneCheckCode.ashx";
	private static String VERIFY_CODE_URL = CURRENT_SERVER
			+ "Users/PhoneVerifyCodeCheck.ashx";
	private static String REGISTER_URL = CURRENT_SERVER
			+ "Users/RegisterCheck.ashx";
	private static String ACCOUNT_INFO_URL = CURRENT_SERVER
			+ "Users/UpdateSafeSet.ashx";
	
	
	private static String ALARM_RECORD_LIST_URL = CURRENT_SERVER
			+ "Alarm/AlarmRecordEx.ashx";
	
	private static String EXIT_APPLICATION_URL = CURRENT_SERVER
			+ "Users/Logout.ashx";
	private static String MODIFY_LOGIN_PASSWORD_URL = CURRENT_SERVER
			+ "Users/ModifyPwd.ashx";

	private static String BIND_DEVICE_ACCOUNT = CURRENT_SERVER
			+ "Account/Bind/BindAccountEx.ashx";
	
	private static String SEARCH_BIND_DEVICE_ACCOUNT = CURRENT_SERVER
			+ "Account/Bind/SearchBindAccountEx.ashx";
	
	private static String REMOVE_BIND_DEVICE_ACCOUNT = CURRENT_SERVER
			+ "Account/Bind/RemoveBindEx.ashx";
	
	private static String DEVICE_LIST_URL = CURRENT_SERVER
			+ "Account/Bind/SearchBindDev.ashx";
	
	private static String MODIFY_NIKE_NAME = CURRENT_SERVER
			+ "Account/Bind/ModifyNickname.ashx";
	private static String ALARM_MSSAGE_URL="http://192.168.1.222/"
			+ "Alarm/AlarmRecordEx.ashx";
	private static String SYSTEM_MESSAGE_URL="http://cloudlinks.cn/"+
			"business/seller/recommendinfo.ashx";
	private static String LOGO_URL="http://cloudlinks.cn/"+
			"AppInfo/getappstartinfo.ashx";
	private static String MALL_URL="http://cloudlinks.cn/"+
			"AppInfo/getstorelinks.ashx";
	private static String STORE_ID_URL="http://cloudlinks.cn/"+
			"AppInfo/SetStoreID.ashx";
	
	private static final String SERVER1 = "http://api1.cloudlinks.cn/";
	private static final String SERVER2 = "http://api2.cloudlinks.cn/";
	private static final String SERVER3 = "http://api3.cloud-links.net/";
	private static final String SERVER4 = "http://api4.cloud-links.net/";
	private static boolean isInit = false;
	private static String[] servers = new String[4];
	
	private static int reconnect_count = 0;

	public static JSONObject login(String username, String password) {
		username = "+86-"+username;
		JSONObject jObject = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		MD5 md = new MD5();
		params.add(new BasicNameValuePair("User", username));
//		params.add(new BasicNameValuePair("privId", privId));
		params.add(new BasicNameValuePair("Pwd", md.getMD5ofStr(password)));
		params.add(new BasicNameValuePair("VersionFlag", "1"));
		params.add(new BasicNameValuePair("AppOS", "3"));
		params.add(new BasicNameValuePair("AppVersion", MyUtils.getBitProcessingVersion()));
		try {
			if (!isInit) {
				initServer();
			}
			updateUrl(CURRENT_SERVER);
			jObject = new JSONObject(doPost(params, LOGIN_URL));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}
	
	private static void initServer() {
		randomServer();
		reconnect_count = 0;
		CURRENT_SERVER = servers[0];
		isInit = true;
	}
	
	private static void randomServer() {
		Random random = new Random();
		int value = random.nextInt(2);
		if(value==0){
			servers[0] = SERVER1;
			servers[1] = SERVER2;
		}else{
			servers[0] = SERVER2;
			servers[1] = SERVER1;
		}
		value = random.nextInt(2);
		if(value==0){
			servers[2] = SERVER3;
			servers[3] = SERVER4;
		}else{
			servers[2] = SERVER4;
			servers[3] = SERVER3;
		}
		CURRENT_SERVER = servers[0];
	}
	
	public static String doPost(List<NameValuePair> params, String url)
			throws Exception {
		
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		httpPost.setEntity(entity);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				10000);
		try {
			HttpResponse httpResp = httpClient.execute(httpPost);
			int http_code;
			if ((http_code = httpResp.getStatusLine().getStatusCode()) == 200) {
				result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
			} else {
				throw new Exception();
			}
			try {
				JSONObject jObject = new JSONObject(result);
				int error_code = jObject.getInt("error_code");
				if (error_code == 1 || error_code == 29 || error_code == 999) {
					throw new Exception();
				}
			} catch (Exception e) {
				throw new Exception();
			}
			randomServer();
			reconnect_count = 0;

		} catch (Exception e) {
			reconnect_count++;
			if (reconnect_count <= 3) {
				CURRENT_SERVER = servers[reconnect_count];
				result = "{\"error_code\":998}";
			} else {
				randomServer();
				reconnect_count = 0;
				result = "{\"error_code\":999}";
			}
		}

		return result;
	}
	

	private static void updateUrl(String server) {
		LOGIN_URL = server + "Users/LoginCheck.ashx";
		GETPHONE_CODE_URL = server + "Users/PhoneCheckCode.ashx";
		VERIFY_CODE_URL = server + "Users/PhoneVerifyCodeCheck.ashx";
		REGISTER_URL = server + "Users/RegisterCheck.ashx";
		ACCOUNT_INFO_URL = server + "Users/UpdateSafeSet.ashx";
		
		ALARM_RECORD_LIST_URL = server + "Alarm/AlarmRecordEx.ashx";
		EXIT_APPLICATION_URL = server + "Users/Logout.ashx";
		MODIFY_LOGIN_PASSWORD_URL = server + "Users/ModifyPwd.ashx";
		BIND_DEVICE_ACCOUNT = server + "Account/Bind/BindAccountEx.ashx";
		SEARCH_BIND_DEVICE_ACCOUNT = server + "Account/Bind/SearchBindAccountEx.ashx";
		REMOVE_BIND_DEVICE_ACCOUNT = server + "Account/Bind/RemoveBindEx.ashx";
		DEVICE_LIST_URL = server + "Account/Bind/SearchBindDev.ashx";
		MODIFY_NIKE_NAME = server + "Account/Bind/ModifyNickname.ashx";
		ALARM_MSSAGE_URL="http://192.168.1.222/"+"Alarm/AlarmRecordEx.ashx";
		
	}
	
	public static LoginResult createLoginResult(JSONObject jObject) {
		LoginResult result = null;
		result = new LoginResult(jObject);
		return result;
	}
}
