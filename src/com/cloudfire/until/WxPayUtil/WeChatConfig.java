package com.cloudfire.until.WxPayUtil;

public class WeChatConfig {
	public static final String APPID = "";//公众账号ID
	public static final String MCH_ID = ""; //商户号
	public static final String KEY = ""; //商户密钥
	
	//APP和网页支付提交用户端ip，native支付填调用微信支付API的机器IP，服务器IP
	public static final String SPBILL_CREATE_IP = "127.0.0.1";
	//接收微信支付异步通知回调地址，通知url必须为直接访问的url，不能携带参数
	public static final String NOTIFY_URL = "http://localhost:8080/fireSystem/wxPayCallBack.do";
	//支付方式  JSAPI, NATIVE ,APP
	public static final String TRADE_TYPE= "NATIVE";
	
	//微信支付  统一下单地址
	public static final String PLACEANORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
}
