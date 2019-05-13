package com.cloudfire.until;

import java.io.IOException;

import org.json.JSONException;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.SmsVoicePromptSender;
import com.github.qcloudsms.SmsVoicePromptSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

public class PayNotice {
	//�������ű���֪ͨ
	public static String sentMessage1(String phone1, String mac, int day) {
    	//String sentext = "���ã�"+devName+""+address+"��⵽�쳣���뼰ʱ����ֳ��������������";
		String sentext = "���ã���������豸��Ϊ"+mac+"���豸����ʣ"+day+"�쵽�ڣ�Ϊ�˲�Ӱ����������ʹ�ã��뾡���ֵ��";
    	// ����Ӧ��SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400��ͷ
    	// ����Ӧ��SDK AppKey
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
	
	//�绰��������֪ͨ
	public static String telphone1(String phone1, String mac, int day){
		//String sentext = "���ã���������豸��Ϊ"+mac+"���豸����ʣ"+day+"�쵽�ڣ�Ϊ�˲�Ӱ����������ʹ�ã��뾡���ֵ��";
		String sentext = "���ã�"+mac+"��"+day+"��⵽�쳣���뼰ʱ����ֳ��������������";
    	// ����Ӧ��SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400��ͷ
    	// ����Ӧ��SDK AppKey
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
    	String sentext = "���ã�"+devName+"��"+address+"��⵽�쳣��ʱ����ֳ��������������";
    	// ����Ӧ��SDK AppID
    	int appid = Constant.NOTICE_APPID; // 1400��ͷ
    	// ����Ӧ��SDK AppKey
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
