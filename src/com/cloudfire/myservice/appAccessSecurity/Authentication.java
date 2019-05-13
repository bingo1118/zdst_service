package com.cloudfire.myservice.appAccessSecurity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

/**
 *  Auth:huoqu token
 *  This interface is used to authenticate third-party systems before third-party systems access open APIs.
 */
public class Authentication {
    @SuppressWarnings({ "unchecked" })
    public static String getAccessToken(String appId,String secret){
    	String result = "";
    	
        // Two-Way Authentication
        try {
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();

			if(StringUtils.isBlank(appId)&&StringUtils.isBlank(secret)){
				appId = Constant.APPID;
				secret = Constant.SECRET;
			}
			String urlLogin = Constant.APP_AUTH;

			Map<String, String> param = new HashMap<>();
			param.put("appId", appId);
			param.put("secret", secret);
			System.out.println("Authentication--getAccessToken");

			StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, param);

			System.out.println("app auth success,return accessToken:");
			System.out.print(responseLogin.getStatusLine());
			System.out.println(responseLogin.getContent());
			System.out.println();

			//resolve the value of accessToken from responseLogin.
			Map<String, String> data = new HashMap<>();
			data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
			result = data.get("accessToken");
			System.out.println("accessToken:" + result);
		} catch (Exception e) {
			System.out.println("huoqu accessToKen error");
//			e.printStackTrace();
		}
        return result;
    }
}
