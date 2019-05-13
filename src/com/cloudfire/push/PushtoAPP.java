package com.cloudfire.push;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cloudfire.dao.LoginDao;
import com.cloudfire.dao.impl.LoginDaoImpl;
import com.gexin.fastjson.JSONObject;
import com.gexin.fastjson.serializer.SerializerFeature;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;

public class PushtoAPP {
	private final static Log log = LogFactory.getLog(PushtoAPP.class);
	
	public void alarmPushToApp(Object content,List<String> aliasList){
        if(aliasList==null){
        	return;
        }
        LoginDao loginDao = new LoginDaoImpl(); 					//add by lzz at 2017-5-19
        String appIdstr = "";
        String appKeystr = "";
        String masterSecretstr = "";
        for(String alias :aliasList){
        	
    		appIdstr = GeTuiConfiguration.appId_zdst;		//add by lzz at 2017-5-19
    		appKeystr = GeTuiConfiguration.appKey_zdst;
    		masterSecretstr = GeTuiConfiguration.masterSecret_zdst;
        														//add by lzz at 2017-5-19
        	
        	System.out.print("&&&&&pushappid"+appIdstr);
        	IGtPush push = new IGtPush(GeTuiConfiguration.host, appKeystr, masterSecretstr);
        	
        	TransmissionTemplate template = transmissionTemplateDemo(content,appIdstr,appKeystr);
            SingleMessage message = new SingleMessage();
            message.setOffline(true);
            // ������Чʱ�䣬��λΪ���룬��ѡ
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            // ��ѡ��1Ϊwifi��0Ϊ���������绷���������ֻ����ڵ���������������Ƿ��·�
            message.setPushNetWorkType(0); 
            Target target = new Target();
            target.setAppId(appIdstr);
        	
        	
        	
        	
        	target.setAlias(alias);
            IPushResult ret = null;
            try {
        		ret = push.pushMessageToSingle(message, target);
            } catch (RequestException e) {
                e.printStackTrace();
                ret = push.pushMessageToSingle(message, target, e.getRequestId());
            }
            if (ret != null) {
            	log.debug("alias="+alias+"------------"+ret.getResponse().toString());
            } else {
            	log.debug("alias="+alias+"------------��������Ӧ�쳣");
            }
        }
        
	}

	public static TransmissionTemplate transmissionTemplateDemo(Object content,String appId,String appKey) {
		String jsonString = JSONObject.toJSONString(content,SerializerFeature.WriteMapNullValue);
	    TransmissionTemplate template = new TransmissionTemplate();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // ͸����Ϣ���ã�1Ϊǿ������Ӧ�ã��ͻ��˽��յ���Ϣ��ͻ���������Ӧ�ã�2Ϊ�ȴ�Ӧ������
	    template.setTransmissionType(2);
	    template.setTransmissionContent(jsonString);
	    // ���ö�ʱչʾʱ��
	    // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
	    //	    template.setTransmissionType()
	    return template;
	}
	
	public void pushNotificationToIOS(Object content,Map<String,String> iosMap){
		
		LoginDao loginDao = new LoginDaoImpl(); 					//add by lzz at 2017-5-19
        String appIdstr = "";
        String appKeystr = "";
        String masterSecretstr = "";
        IPushResult mIPushResult=null;
        for (String userId : iosMap.keySet()) {
        	
    		appIdstr = GeTuiConfiguration.appId_zdst;		//add by lzz at 2017-5-19
    		appKeystr = GeTuiConfiguration.appKey_zdst;
    		masterSecretstr = GeTuiConfiguration.masterSecret_zdst;
      
        	
        	IGtPush push = new IGtPush(GeTuiConfiguration.host, appKeystr, masterSecretstr);
    		TransmissionTemplate template = getTemplate(appIdstr,appKeystr);
    		SingleMessage message = new SingleMessage();
            message.setOffline(true);
            // ������Чʱ�䣬��λΪ���룬��ѡ
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            // ��ѡ��1Ϊwifi��0Ϊ���������绷���������ֻ����ڵ���������������Ƿ��·�
            message.setPushNetWorkType(0); 
            
            SingleMessage mSingleMessage = new SingleMessage();
            mSingleMessage.setOffline(true);
            // ������Чʱ�䣬��λΪ���룬��ѡ
            mSingleMessage.setOfflineExpireTime(24 * 3600 * 1000);
            mSingleMessage.setData(getTemplate2(content,appIdstr,appKeystr));
            // ��ѡ��1Ϊwifi��0Ϊ���������绷���������ֻ����ڵ���������������Ƿ��·�
            mSingleMessage.setPushNetWorkType(0); 
            Target target = new Target();
            target.setAppId(appIdstr);
        	
        	
        	
			String iosCid = iosMap.get(userId);
			try {
				mIPushResult = push.pushAPNMessageToSingle(appIdstr, iosCid, message);
			} catch (Exception e) {
				System.out.println("mIPushResult:is error");
			}
			if (mIPushResult != null) {
	        	log.debug("mIPushResult==="+mIPushResult.getResponse());
	        } else {
	        	log.debug("userId="+userId+"------------��������Ӧ�쳣");
	        }
			target.setAlias(userId);
			IPushResult iPushResult = push.pushMessageToSingle(mSingleMessage, target);
			if (iPushResult != null) {
	        	log.debug("iPushResult==="+iPushResult.getResponse());
	        } else {
	        	log.debug("userId="+userId+"------------��������Ӧ�쳣");
	        }
		}
	}
	
	public static TransmissionTemplate getTemplate(String appId,String appKey) {
		TransmissionTemplate template = new TransmissionTemplate ();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // ����֪ͨ��ͼ��
	    APNPayload payload = new APNPayload();
	    //���������ֻ����ϼ�1��ʾ������Ϊ-1ʱ�������������ϼ�1��ʾ������Ϊ����ʱ����ʾָ������
	    payload.setContentAvailable(1);
	    payload.setSound("default");
//	    payload.setCategory("�ɿͻ��˶���");
	    //�ֵ�ģʽʹ������
	    payload.setAlertMsg(getDictionaryAlertMsg());
	    template.setAPNInfo(payload);
	    return template;
	}
	
	public static TransmissionTemplate getTemplate2(Object content,String appId,String appKey) {
		String jsonString = JSONObject.toJSONString(content,SerializerFeature.WriteMapNullValue);
	    TransmissionTemplate template = new TransmissionTemplate();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    template.setTransmissionContent(jsonString);
	    template.setTransmissionType(2);
	    return template;
	 }
	

	
	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(){
	    APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
	    alertMsg.setBody("���������������鿴");
	    return alertMsg;
	}
	
	/**
	 * �򵥸��û�����֪ͨ
	 * @return
	 */
	public static boolean PushtoSingle(String alias,String contant){
		boolean isSuccess=false;
		String appId = GeTuiConfiguration.appId_zdst;
	    String appKey = GeTuiConfiguration.appKey_zdst;
	    String masterSecret = GeTuiConfiguration.masterSecret_zdst;

	    String host = "http://sdk.open.api.igexin.com/apiex.htm";
		IGtPush push = new IGtPush(host, appKey, masterSecret);
        NotificationTemplate template = getTemplate(contant);
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // ������Чʱ�䣬��λΪ���룬��ѡ
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // ��ѡ��1Ϊwifi��0Ϊ���������绷���������ֻ����ڵ���������������Ƿ��·�
        message.setPushNetWorkType(0);
        Target target = new Target();
        target.setAppId(appId);
        target.setAlias(alias);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
        	if(ret.getResponse().toString().contains("result=ok")){
        		isSuccess=true;
        	}
            System.out.println("************"+ret.getResponse().toString());
        } else {
            System.out.println("��������Ӧ�쳣");
        }
		return isSuccess;
		
	}
	
	public static NotificationTemplate getTemplate(String contant) {
		NotificationTemplate template = new NotificationTemplate();
        // ����APPID��APPKEY
        template.setAppId(GeTuiConfiguration.appId_zdst);
        template.setAppkey(GeTuiConfiguration.appKey_zdst);

        Style0 style = new Style0();
        // ����֪ͨ������������
        style.setTitle("֪ͨ");
        style.setText(contant);
        // ����֪ͨ��ͼ��
        style.setLogo("ic_launcher.png");
        // ����֪ͨ�Ƿ����壬�𶯣����߿����
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        return template;
    }
}
