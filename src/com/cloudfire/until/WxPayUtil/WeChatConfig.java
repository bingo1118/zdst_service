package com.cloudfire.until.WxPayUtil;

public class WeChatConfig {
	public static final String APPID = "";//�����˺�ID
	public static final String MCH_ID = ""; //�̻���
	public static final String KEY = ""; //�̻���Կ
	
	//APP����ҳ֧���ύ�û���ip��native֧�������΢��֧��API�Ļ���IP��������IP
	public static final String SPBILL_CREATE_IP = "127.0.0.1";
	//����΢��֧���첽֪ͨ�ص���ַ��֪ͨurl����Ϊֱ�ӷ��ʵ�url������Я������
	public static final String NOTIFY_URL = "http://localhost:8080/fireSystem/wxPayCallBack.do";
	//֧����ʽ  JSAPI, NATIVE ,APP
	public static final String TRADE_TYPE= "NATIVE";
	
	//΢��֧��  ͳһ�µ���ַ
	public static final String PLACEANORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
}
