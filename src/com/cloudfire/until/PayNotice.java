package com.cloudfire.until;

import java.io.IOException;

import org.json.JSONException;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.SmsVoicePromptSender;
import com.github.qcloudsms.SmsVoicePromptSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

public class PayNotice {
	//单发短信报警通知
	public static String sentMessage1(String phone1, String mac, int day) {
    	//String sentext = "您好，"+devName+""+address+"检测到异常，请及时检查现场情况并作出处理。";
		String sentext = "您好，您管理的设备号为"+mac+"的设备，还剩"+day+"天到期，为了不影响您的正常使用，请尽快充值。";
    	// 短信应用SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400开头
    	// 短信应用SDK AppKey
    	String appkey = Constant.NOTICE_APPKEY;
    	
    	SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
    	SmsSingleSenderResult result = null;
    	 try {
    		 result=ssender.send(0, "86", phone1, sentext, "", "");
    	 } catch (HTTPException e) {
 			e.printStackTrace();
 		} catch (JSONException e) {
 			e.printStackTrace();
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
		return result.toString();
    }
	
	//电话语音报警通知
	public static String telphone1(String phone1, String mac, int day){
		//String sentext = "您好，您管理的设备号为"+mac+"的设备，还剩"+day+"天到期，为了不影响您的正常使用，请尽快充值。";
		String sentext = "您好，"+mac+"在"+day+"检测到异常，请及时检查现场情况并作出处理。";
    	// 短信应用SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400开头
    	// 短信应用SDK AppKey
    	String appkey = Constant.NOTICE_APPKEY;
    	
    	SmsVoicePromptSender vpsender = new SmsVoicePromptSender(appid, appkey);
    	SmsVoicePromptSenderResult result = null;
    	try {
				result = vpsender.send("86", phone1, 2, 2, sentext, "");
    	} catch (HTTPException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return result.toString();
    }
	
	public static String telphone(String phone1,String devName,String address){
    	String sentext = "您好，"+devName+"在"+address+"检测到异常，时检查现场情况并作出处理。";
    	// 短信应用SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400开头
    	// 短信应用SDK AppKey
    	String appkey = Constant.NOTICE_APPKEY;
    	
    	SmsVoicePromptSender vpsender = new SmsVoicePromptSender(appid, appkey);
    	SmsVoicePromptSenderResult result = null;
    	try {
				result = vpsender.send("86", phone1, 2, 2, sentext, "");
    		} catch (HTTPException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       
    	
    	return result.toString();
    }
	
	public static void main(String[] args) {
		//String a = PayNotice.telphone1("13319469604", "7B820430", 10);
		//System.out.println(a);
		
		String a1 = PayNotice.telphone("13319469604", "7B820430", "10");
		System.out.println(a1);
		//String b = PayNotice.sentMessage1("13319469604", "7B820430", 10);
		//System.out.println(b);
	}
	

}
