package com.cloudfire.entity;

import org.json.JSONObject;

import com.cloudfire.until.MyUtils;

import java.io.Serializable;

public class LoginResult implements Serializable {
	public String contactId;
	public String rCode1;
	public String rCode2;
	public String phone;
	public String email;
	public String sessionId;
	public String countryCode;
	public String error_code;
	public LoginResult(JSONObject json){
		init(json);
	}
	private void init(JSONObject json){
		try{
			error_code = json.getString("error_code");
			contactId = json.getString("UserID");
			rCode1 = json.getString("P2PVerifyCode1");
			rCode2 = json.getString("P2PVerifyCode2");
			phone = json.getString("PhoneNO");
			email = json.getString("Email");
			sessionId = json.getString("SessionID");
			countryCode = json.getString("CountryCode");
			try{
				contactId = "0"+String.valueOf((Integer.parseInt(contactId)&0x7fffffff));
			}catch(Exception e){
				
			}
		}catch(Exception e){
			contactId = "";
			rCode1 = "";
			rCode2 = "";
			phone = "";
			email = "";
			sessionId = "";
			countryCode = "";
			if(!MyUtils.isNumeric(error_code)){
				error_code = String.valueOf("997");
			}
		}
	}

}