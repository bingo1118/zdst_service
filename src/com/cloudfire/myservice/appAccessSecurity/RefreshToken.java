package com.cloudfire.myservice.appAccessSecurity;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cloudfire.until.Constant;
import com.cloudfire.until.HttpsUtil;
import com.cloudfire.until.JsonUtil;
import com.cloudfire.until.StreamClosedHttpResponse;

/**
 *  Refresh Token :
 *  This interface is used by an NA to obtain an available access token when the access token is to expire.
 */
public class RefreshToken {

    public static void getRefreshToken(String appId,String secret){

        // Two-Way Authentication
        try {
			HttpsUtil httpsUtil = new HttpsUtil();
			httpsUtil.initSSLConfigForTwoWay();

			// get refreshToken
			String refreshToken = getRefreshToken(httpsUtil); 
			if(StringUtils.isBlank(appId)&&StringUtils.isBlank(secret)){
				appId = Constant.APPID;
				secret = Constant.SECRET;
			}
			String urlRefreshToken = Constant.REFRESH_TOKEN; 

			Map<String, Object> param_reg = new HashMap<>();
			param_reg.put("appId", appId);
			param_reg.put("secret", secret);
			param_reg.put("refreshToken", refreshToken);

			String jsonRequest = JsonUtil.jsonObj2Sting(param_reg);
			StreamClosedHttpResponse bodyRefreshToken = httpsUtil.doPostJsonGetStatusLine(urlRefreshToken, jsonRequest);

			System.out.println("RefreshToken, response content:");
			System.out.print(bodyRefreshToken.getStatusLine());
			System.out.println(bodyRefreshToken.getContent());
			System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * get refreshToken
     * */
    @SuppressWarnings("unchecked")
    public static String getRefreshToken(HttpsUtil httpsUtil) throws Exception {

        String appId = Constant.APPID;
        String secret = Constant.SECRET;
        String urlLogin = Constant.APP_AUTH;

        Map<String, String> paramLogin = new HashMap<>();
        paramLogin.put("appId", appId);
        paramLogin.put("secret", secret);

        StreamClosedHttpResponse responseLogin = httpsUtil.doPostFormUrlEncodedGetStatusLine(urlLogin, paramLogin);

        System.out.println("app auth success,return accessToken:");
        System.out.print(responseLogin.getStatusLine());
        System.out.println(responseLogin.getContent());
        System.out.println();

        Map<String, String> data = new HashMap<>();
        data = JsonUtil.jsonString2SimpleObj(responseLogin.getContent(), data.getClass());
        return data.get("refreshToken");
    }
}
