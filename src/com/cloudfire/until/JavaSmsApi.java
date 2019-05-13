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
 * ����http�ӿڵ�java�������ʾ��
 * ����Apache HttpClient 4.3
 *
 * @author songchao
 * @since 2015-04-03
 * ����֪ͨ ģ������ı��뷽ʽ     
 * String tpl_value=URLEncoder.encode("device_name", ENCODING) + "=" + 
          		 URLEncoder.encode("����", ENCODING) + "&" + 
          		 URLEncoder.encode("address", ENCODING) + "=" + 
          		 URLEncoder.encode("������", ENCODING);
 * ����-���� ģ������ı��뷽ʽ
 * String tpl_value=URLEncoder.encode("#device_name#", ENCODING) + "=" + 
          		 URLEncoder.encode("����", ENCODING) + "&" + 
          		 URLEncoder.encode("#address#", ENCODING) + "=" + 
          		 URLEncoder.encode("������", ENCODING);
 */
    
public class JavaSmsApi {

	
    //���˻���Ϣ��http��ַ
    private static String URI_GET_USER_INFO =
        "https://sms.yunpian.com/v2/user/get.json";

    //����ƥ��ģ�巢�ͽӿڵ�http��ַ
    private static String URI_SEND_SMS =
        "https://sms.yunpian.com/v2/sms/single_send.json";

    //ָ��ģ�嵥���ӿڵ�http��ַ
    private static String URI_TPL_SEND_SMS =
        "https://sms.yunpian.com/v2/sms/tpl_single_send.json";

    //ָ��ģ��Ⱥ���ӿڵ�http��ַ
//    private static String URI_TPL_BATCH_SEND_SMS =
//            "https://sms.yunpian.com/v2/sms/tpl_batch_send.json";
    
    //����������֤��ӿڵ�http��ַ
//    private static String URI_SEND_VOICE =
//        "https://voice.yunpian.com/v2/voice/send.json";
    
   //��������֪ͨ�ӿڵ�http��ַ
    private static String URI_SEND_VOICE2 =
//    		"https://www.yunpian.com/doc/zh_CN/voiceNotify/notify_send.html";
    	"https://voice.yunpian.com/v2/voice/tpl_notify.json";
    

    //�����С����й�ϵ�Ľӿ�http��ַ
//    private static String URI_SEND_BIND =
//        "https://call.yunpian.com/v2/call/bind.json";

    //������С����й�ϵ�Ľӿ�http��ַ
//    private static String URI_SEND_UNBIND =
//        "https://call.yunpian.com/v2/call/unbind.json";

    //�����ʽ�����ͱ����ʽͳһ��UTF-8
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
            //�޸�Ϊ��Ҫ���͵��ֻ���0
//            String mobile = "15273535106";
//            String mobile = "18312286056";
            String mobile = "18011715889";
            
//          //���ö�Ӧ��ģ�����ֵ
            String tpl_value=
//            		"device_name=smoke"+"&"+"address=HONGKONG";
//            		URLEncoder.encode("device_name=smoke&address=HONGKONG",ENCODING);
          		URLEncoder.encode("#device_name#", ENCODING) + "=" + 
          		 URLEncoder.encode("4.28�����̸�2", ENCODING) + "&" + 
          		 URLEncoder.encode("#address#", ENCODING) + "=" + 
          		 URLEncoder.encode("С�ܲ��Զ���", ENCODING);
       
          
//        String tpl_value = URLEncoder.encode("#code#", ENCODING) + "=" +
//                URLEncoder.encode("1234", ENCODING) + "&" + URLEncoder.encode(
//                    "#company#", ENCODING) + "=" + URLEncoder.encode("��Ƭ��",
//                    ENCODING);
            
          
            /**************** ���˻���Ϣ����ʾ�� *****************/
           System.out.println(JavaSmsApi.getUserInfo());
            
            /**************** ʹ������ƥ��ģ��ӿڷ�����(�Ƽ�) *****************/
            //������Ҫ���͵�����(���ݱ����ĳ��ģ��ƥ�䡣��������ƥ�����ϵͳ�ṩ��1��ģ�壩
//            String text = "����Ƭ����������֤����1234";
            //�����ŵ���ʾ��
            // System.out.println(JavaSmsApi.sendSms( text, mobile));

           /****************ָ��ģ��ӿڷ���������,��Ϊ��Ӫ�̲�֧����ʱ�Ȳ��� *****************/ 	
//            System.out.println(JavaSmsApi.sendVoice2( mobile, tpl_value));
            
           /****************ָ��ģ�巢�Ͷ���֪ͨ *****************/ 	
//            System.out.println(tpl_value);
            System.out.println(JavaSmsApi.tplSendSms( apikey,  tpl_id,  tpl_value,
                     mobile));
            
            /**************** ʹ�ýӿڷ�������֤�� *****************/
//            String code = "1234";
//            System.out.println(JavaSmsApi.sendVoice(mobile ,code));
//          
            
            /**************** ʹ�ýӿڰ������к��� *****************/
//            String from = "+86130xxxxxxxx";
//            String to = "+86131xxxxxxxx";
//            Integer duration = 30 * 60; // ��30����
            //System.out.println(JavaSmsApi.bindCall( from ,to , duration));

            /**************** ʹ�ýӿڽ�������к��� *****************/
            //System.out.println(JavaSmsApi.unbindCall( from, to));
        }

    /**
     * ȡ�˻���Ϣ
     *
     * @return json��ʽ�ַ���
     * @throws java.io.IOException
     */
    public static String getUserInfo() throws IOException,
        URISyntaxException {
            Map < String, String > params = new HashMap < String, String > ();
            params.put("apikey", apikey);
            return post(URI_GET_USER_INFO, params);
        }

    /**
     * ƥ��ģ��ӿڷ�����������  �ݲ�֧��
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
     * ����ƥ��ģ��ӿڷ�����
     *
     * @param apikey apikey
     * @param text   ����������
     * @param mobile �����ܵ��ֻ���
     * @return json��ʽ�ַ���
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
     * ͨ��ģ�巢�Ͷ���(���Ƽ�),�����Ըýӿڿ���ֱ��ʹ�á�
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
//    			URLEncoder.encode("#device_name#="+device_name+"&"+"#address#="+address,ENCODING); // ����֪ͨ���������ʽ
        Map < String, String > params = new HashMap < String, String > ();
        params.put("apikey", apikey);
        params.put("tpl_id", String.valueOf(tpl_id));
        params.put("tpl_value", tpl_value);
        params.put("mobile", mobile);
//        return post(URI_SEND_VOICE2, params);  //����֪ͨ
        return post(URI_TPL_SEND_SMS, params);  //����֪ͨ
    }

    /**
     * ͨ���ӿڷ���������֤��
     * @param apikey apikey
     * @param mobile ���յ��ֻ���
     * @param code   ��֤��
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
     * ͨ���ӿڰ������к���
     * @param apikey apikey
     * @param from ����
     * @param to   ����
     * @param duration ��Чʱ������λ����
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
     * ͨ���ӿڽ��������к���
     * @param apikey apikey
     * @param from ����
     * @param to   ����
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
     * ����HttpClient 3.1��ͨ��POST����
     *
     * @param url       �ύ��URL
     * @param paramsMap �ύ<������ֵ>Map
     * @return �ύ��Ӧ
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
