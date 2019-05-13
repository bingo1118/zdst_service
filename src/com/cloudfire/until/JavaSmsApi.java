package com.cloudfire.until;



/**
 * Created by bingone on 15/12/16.
 */
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
    
/**
 * 短信http接口的java代码调用示例
 * 基于Apache HttpClient 4.3
 *
 * @author songchao
 * @since 2015-04-03
 * 语音通知 模板参数的编码方式     
 * String tpl_value=URLEncoder.encode("device_name", ENCODING) + "=" + 
          		 URLEncoder.encode("烟雾", ENCODING) + "&" + 
          		 URLEncoder.encode("address", ENCODING) + "=" + 
          		 URLEncoder.encode("测试区", ENCODING);
 * 语音-短信 模板参数的编码方式
 * String tpl_value=URLEncoder.encode("#device_name#", ENCODING) + "=" + 
          		 URLEncoder.encode("烟雾", ENCODING) + "&" + 
          		 URLEncoder.encode("#address#", ENCODING) + "=" + 
          		 URLEncoder.encode("测试区", ENCODING);
 */
    
public class JavaSmsApi {

	
    //查账户信息的http地址
    private static String URI_GET_USER_INFO =
        "https://sms.yunpian.com/v2/user/get.json";

    //智能匹配模板发送接口的http地址
    private static String URI_SEND_SMS =
        "https://sms.yunpian.com/v2/sms/single_send.json";

    //指定模板单发接口的http地址
    private static String URI_TPL_SEND_SMS =
        "https://sms.yunpian.com/v2/sms/tpl_single_send.json";

    //指定模板群发接口的http地址
//    private static String URI_TPL_BATCH_SEND_SMS =
//            "https://sms.yunpian.com/v2/sms/tpl_batch_send.json";
    
    //发送语音验证码接口的http地址
//    private static String URI_SEND_VOICE =
//        "https://voice.yunpian.com/v2/voice/send.json";
    
   //发送语音通知接口的http地址
    private static String URI_SEND_VOICE2 =
//    		"https://www.yunpian.com/doc/zh_CN/voiceNotify/notify_send.html";
    	"https://voice.yunpian.com/v2/voice/tpl_notify.json";
    

    //绑定主叫、被叫关系的接口http地址
//    private static String URI_SEND_BIND =
//        "https://call.yunpian.com/v2/call/bind.json";

    //解绑主叫、被叫关系的接口http地址
//    private static String URI_SEND_UNBIND =
//        "https://call.yunpian.com/v2/call/unbind.json";

    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";
    
    //apikey
//    private static  String apikey = "a972cfebcf157229816866255dc19935";
    private static  String apikey ="93ff92c55ac55331f7cf1b01048c6c66";
    
    //tpl_id
//    private static long tpl_id =  2145698;
//    private static long tpl_id = 2129984;
    private static long tpl_id = 2194208;
    
    
    public static void main(String[] args) throws IOException,
        URISyntaxException {
            //修改为您要发送的手机号0
//            String mobile = "15273535106";
//            String mobile = "18312286056";
            String mobile = "18011715889";
            
//          //设置对应的模板变量值
            String tpl_value=
//            		"device_name=smoke"+"&"+"address=HONGKONG";
//            		URLEncoder.encode("device_name=smoke&address=HONGKONG",ENCODING);
          		URLEncoder.encode("#device_name#", ENCODING) + "=" + 
          		 URLEncoder.encode("4.28测试烟感2", ENCODING) + "&" + 
          		 URLEncoder.encode("#address#", ENCODING) + "=" + 
          		 URLEncoder.encode("小周测试二区", ENCODING);
       
          
//        String tpl_value = URLEncoder.encode("#code#", ENCODING) + "=" +
//                URLEncoder.encode("1234", ENCODING) + "&" + URLEncoder.encode(
//                    "#company#", ENCODING) + "=" + URLEncoder.encode("云片网",
//                    ENCODING);
            
          
            /**************** 查账户信息调用示例 *****************/
           System.out.println(JavaSmsApi.getUserInfo());
            
            /**************** 使用智能匹配模板接口发短信(推荐) *****************/
            //设置您要发送的内容(内容必须和某个模板匹配。以下例子匹配的是系统提供的1号模板）
//            String text = "【云片网】您的验证码是1234";
            //发短信调用示例
            // System.out.println(JavaSmsApi.sendSms( text, mobile));

           /****************指定模板接口发语音播报,因为运营商不支持暂时先不用 *****************/ 	
//            System.out.println(JavaSmsApi.sendVoice2( mobile, tpl_value));
            
           /****************指定模板发送短信通知 *****************/ 	
//            System.out.println(tpl_value);
            System.out.println(JavaSmsApi.tplSendSms( apikey,  tpl_id,  tpl_value,
                     mobile));
            
            /**************** 使用接口发语音验证码 *****************/
//            String code = "1234";
//            System.out.println(JavaSmsApi.sendVoice(mobile ,code));
//          
            
            /**************** 使用接口绑定主被叫号码 *****************/
//            String from = "+86130xxxxxxxx";
//            String to = "+86131xxxxxxxx";
//            Integer duration = 30 * 60; // 绑定30分钟
            //System.out.println(JavaSmsApi.bindCall( from ,to , duration));

            /**************** 使用接口解绑主被叫号码 *****************/
            //System.out.println(JavaSmsApi.unbindCall( from, to));
        }

    /**
     * 取账户信息
     *
     * @return json格式字符串
     * @throws java.io.IOException
     */
    public static String getUserInfo() throws IOException,
        URISyntaxException {
            Map < String, String > params = new HashMap < String, String > ();
            params.put("apikey", apikey);
            return post(URI_GET_USER_INFO, params);
        }

    /**
     * 匹配模板接口发送语音播报  暂不支持
     * @param apikey
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String sendVoice2(String mobile,String tpl_value) {
    	Map <String,String > params = new HashMap <String, String>();
    	 params.put("apikey", apikey);
         params.put("tpl_id", String.valueOf(tpl_id));
         params.put("tpl_value", tpl_value);
         params.put("mobile", mobile);
    	return post(URI_SEND_VOICE2,params);
    }
    
    /**
     * 智能匹配模板接口发短信
     *
     * @param apikey apikey
     * @param text   　短信内容
     * @param mobile 　接受的手机号
     * @return json格式字符串
     * @throws IOException
     */
    public static String sendSms(String text,  
        String mobile) throws IOException {
        Map < String, String > params = new HashMap < String, String > ();
        params.put("apikey", apikey);
        params.put("text", text);
        params.put("mobile", mobile);
        return post(URI_SEND_SMS, params);
    }

    /**
     * 通过模板发送短信(不推荐),经测试该接口可以直接使用。
     * @param device_name
     * @param address
     * @param mobile
     * @return
     * @throws IOException
     */
    public static String tplSendSms(String apikey, long tpl_id, String tpl_value,
            String mobile) throws IOException {
            Map < String, String > params = new HashMap < String, String > ();
            params.put("apikey", apikey);
            params.put("tpl_id", String.valueOf(tpl_id));
            params.put("tpl_value", tpl_value);
            params.put("mobile", mobile);
            return post(URI_TPL_SEND_SMS, params);
    }
    
    public static String tplSendSms(String device_name,
        String address, String mobile) throws IOException {
    	String tpl_value=
    			URLEncoder.encode("#device_name#", ENCODING) + "=" + 
             		 URLEncoder.encode(device_name, ENCODING) + "&" + 
             		 URLEncoder.encode("#address#", ENCODING) + "=" + 
             		 URLEncoder.encode(address, ENCODING);
//    			URLEncoder.encode("#device_name#="+device_name+"&"+"#address#="+address,ENCODING); // 短信通知参数编码格式
        Map < String, String > params = new HashMap < String, String > ();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
//        return post(URI_SEND_VOICE2, params);  //语音通知
        return post(URI_TPL_SEND_SMS, params);  //短信通知
    }

    /**
     * 通过接口发送语音验证码
     * @param apikey apikey
     * @param mobile 接收的手机号
     * @param code   验证码
     * @return
     */
//    public static String sendVoice( String mobile, String code) {
//        Map < String, String > params = new HashMap < String, String > ();
//        params.put("apikey", apikey);
//        params.put("mobile", mobile);
//        params.put("code", code);
//        return post(URI_SEND_VOICE, params);
//    }

    /**
     * 通过接口绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @param duration 有效时长，单位：秒
     * @return
     */
//    public static String bindCall(String from, String to,
//        Integer duration) {
//        Map < String, String > params = new HashMap < String, String > ();
//        params.put("apikey", apikey);
//        params.put("from", from);
//        params.put("to", to);
//        params.put("duration", String.valueOf(duration));
//        return post(URI_SEND_BIND, params);
//    }

    /**
     * 通过接口解绑绑定主被叫号码
     * @param apikey apikey
     * @param from 主叫
     * @param to   被叫
     * @return
     */
//    public static String unbindCall(String from, String to) {
//        Map < String, String > params = new HashMap < String, String > ();
//        params.put("apikey", apikey);
//        params.put("from", from);
//        params.put("to", to);
//        return post(URI_SEND_UNBIND, params);
//    }

    /**
     * 基于HttpClient 3.1的通用POST方法
     *
     * @param url       提交的URL
     * @param paramsMap 提交<参数，值>Map
     * @return 提交响应
     */
    public static String post(String url, Map < String, String > paramsMap) {
       HttpClient client = new HttpClient();
        String responseText = "";
        try {
            PostMethod method = new PostMethod(url);
            if (paramsMap != null) {
                NameValuePair[] namePairs = new NameValuePair[paramsMap.size()];
                int i = 0;
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new NameValuePair(param.getKey(),param.getValue());
                    namePairs[i++] = pair;
                }
                method.setRequestBody(namePairs);
                HttpMethodParams param = method.getParams();
                param.setContentCharset(ENCODING);
            }
            
           client.executeMethod(method);
           responseText=method.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return responseText;
    }
}
