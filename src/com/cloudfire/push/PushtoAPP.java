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
            // 离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
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
            	log.debug("alias="+alias+"------------服务器响应异常");
            }
        }
        
	}

	public static TransmissionTemplate transmissionTemplateDemo(Object content,String appId,String appKey) {
		String jsonString = JSONObject.toJSONString(content,SerializerFeature.WriteMapNullValue);
	    TransmissionTemplate template = new TransmissionTemplate();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
	    template.setTransmissionType(2);
	    template.setTransmissionContent(jsonString);
	    // 设置定时展示时间
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
            // 离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
            message.setPushNetWorkType(0); 
            
            SingleMessage mSingleMessage = new SingleMessage();
            mSingleMessage.setOffline(true);
            // 离线有效时间，单位为毫秒，可选
            mSingleMessage.setOfflineExpireTime(24 * 3600 * 1000);
            mSingleMessage.setData(getTemplate2(content,appIdstr,appKeystr));
            // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
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
	        	log.debug("userId="+userId+"------------服务器响应异常");
	        }
			target.setAlias(userId);
			IPushResult iPushResult = push.pushMessageToSingle(mSingleMessage, target);
			if (iPushResult != null) {
	        	log.debug("iPushResult==="+iPushResult.getResponse());
	        } else {
	        	log.debug("userId="+userId+"------------服务器响应异常");
	        }
		}
	}
	
	public static TransmissionTemplate getTemplate(String appId,String appKey) {
		TransmissionTemplate template = new TransmissionTemplate ();
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // 配置通知栏图标
	    APNPayload payload = new APNPayload();
	    //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
	    payload.setContentAvailable(1);
	    payload.setSound("default");
//	    payload.setCategory("由客户端定义");
	    //字典模式使用下者
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
	    alertMsg.setBody("发生报警，请点击查看");
	    return alertMsg;
	}
	
	/**
	 * 向单个用户推送通知
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
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
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
            System.out.println("服务器响应异常");
        }
		return isSuccess;
		
	}
	
	public static NotificationTemplate getTemplate(String contant) {
		NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(GeTuiConfiguration.appId_zdst);
        template.setAppkey(GeTuiConfiguration.appKey_zdst);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle("通知");
        style.setText(contant);
        // 配置通知栏图标
        style.setLogo("ic_launcher.png");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        return template;
    }
}
