package com.cloudfire.myservice.impl;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.cloudfire.action.MsgOneNetEntity;
import com.cloudfire.dao.ToolOneNetDao;
import com.cloudfire.dao.impl.ToolOneNetDaoImpl;
import com.cloudfire.entity.OneNetBody;
import com.cloudfire.entity.OneNetResponse;
import com.cloudfire.myservice.OneNetDeviceService;
import com.cloudfire.until.Constant;
import com.cloudfire.until.OneNetHttpMethod;

public class OneNetDeviceServiceImpl implements OneNetDeviceService {
	private static String basicUrl = "http://api.gd.cmcconenet.com/"; //广东平台
	private static String basicUrl2="http://api.heclouds.com/";  //重庆OneNet平台
	private static String urlDevice = basicUrl + "devices";
	private static String urlResource  = basicUrl + "nbiot";
	private static String urlCommand = urlResource + "/execute";
	private static String urlAllResources = urlResource + "/resources";
	private static String urlObserve = urlResource + "/observe";
	
	
	public static void main(String[] args) {
		
		//添加设备测试案例
//		OneNetBody onb = new OneNetBody();
//		onb.setTitle("jd");
//		onb.setDesc("嘉德测试烟感");
//		String[] tags = {"china","mobile"};
//		onb.setTags(tags);
//		onb.setProtocol("LWM2M");
//		Map<String,Float> location = new HashMap<>();
//		location.put("lon", new Float(106));
//		location.put("lat", new Float(29));
//		location.put("ele", new Float(370));
//		onb.setLocation(location);
//		Map<String,String> auth_info = new HashMap<>();
//		auth_info.put("865820030329028", "865820030329028");
//		onb.setAuth_info(auth_info);
//		onb.setObsv(true);
//		Map<String,String> other = new HashMap<>();
//		other.put("version", "1.0.0");
//		other.put("manu","china mobile");
//		onb.setOther(other);
//		OneNetDeviceService ons = new OneNetDeviceServiceImpl();
//		OneNetResponse result = ons.addDevice(onb,"58");
//		System.out.println(result);
		
		//json转换成OneNetResponse对象案例
//		String  str =  "{\"errno\":6,\"error\":\"invalid parameter: imei exists\"}";
//		JSONObject response = JSONObject.fromObject(str);
//		OneNetResponse result = (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
//		System.out.println(result);
		
		//查询设备信息
//		String device_id = "35435926";
//		OneNetDeviceService ons = new OneNetDeviceServiceImpl();
//		String result = ons.getOneDeviceInfo(device_id,"58");
//		System.out.println(result);
		
		//删除设备
//		String device_id = "34781129";
//		OneNetDeviceService ons = new OneNetDeviceServiceImpl();
//		OneNetResponse result = ons.delDevice(device_id,"58");
//		System.out.println(result);
		
		//获取指定设备的资源列表
//		String imei ="865820031003267";
//		String url = urlAllResources + "?imei="+imei;
//		String result = OneNetHttpMethod.get(url);
//		OneNetDeviceService ons = new OneNetDeviceServiceImpl();
//		String result = ons.getAllResources(imei);
//		String result = ons.readDeviceResource(device_id);
//		System.out.println(result);
		
//		String device_id = "110678488";
//		OneNetDeviceService ons = new OneNetDeviceServiceImpl();
////		String result = ons.getAllResources(imei);
//		String result = ons.sendCommand(device_id);
//		System.out.println(result);
		
		//下发命令
//		String url = basicUrl2+"nbiot/execute";
//		String imei,obj_id,obj_inst_id,res_id,cmd; // 必填
//		imei = "869405031169635";
//		obj_id = "3200";
//		obj_inst_id = "0";
//		res_id = "5750";
//		url +="?imei="+imei+"&obj_id="+obj_id+"&obj_inst_id="+obj_inst_id+"&res_id="+res_id+"&timeout=40";
//		String jsonString = "{\"args\":\"1\"}";
//		String jsonString = "{\"args\":\"2000140100055031169635020001000300010604000100\"}";
//		String result = OneNetHttpMethod.postJson(url, jsonString,Constant.chqapikey);
//		System.out.println(result);
		
		//写资源
		String url = basicUrl2+"nbiot";
		String imei,obj_id,obj_inst_id,res_id,cmd; // 必填
		imei = "869405031169635";
		obj_id = "3200";
		obj_inst_id = "0";
		res_id = "5750";
		cmd = "2000140100055031169635020001000300010604000100";
		String result ="";
		url +="?imei="+imei+"&obj_id="+obj_id+"&obj_inst_id="+obj_inst_id+"&mode=1";
		String jsonString = "{\"data\":[{\"res_id\":\""+res_id+"\",\"val\":\""+cmd+"\"}"+"]}";
		result = OneNetHttpMethod.postJson(url, jsonString,Constant.chqapikey);
	}

	@Override
	public OneNetResponse addDevice(OneNetBody onb,String deviceType) {
		JSONObject jsonObject = JSONObject.fromObject(onb);
		String apikey="";
		String url = "";
		switch(deviceType){
		case "57":
			url =basicUrl+"devices";
			apikey=Constant.gdapikey;
			break;
		case "58":
			url =basicUrl2+"devices";
			apikey =Constant.chqapikey;
			break;
		}
		String resultStr = OneNetHttpMethod.postJson(url,jsonObject.toString(),apikey);
//		return resultStr;
		JSONObject response = JSONObject.fromObject(resultStr);
	    return  (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
	}
	
	@Override
	public String updateDevice(OneNetBody onb) {
		JSONObject jsonObject = JSONObject.fromObject(onb);
		String url = urlDevice;
		String resultStr = OneNetHttpMethod.putJson(url,jsonObject.toString());
		return resultStr;
	}

	@Override
	public String getOneDeviceInfo(String device_id,String deviceType) {
		String apikey="";
		String url = "";
		if ("57".equals(deviceType)){
			url =basicUrl+"devices/"+device_id;
			apikey=Constant.gdapikey;
		} else if ("58".equals(deviceType)) {
			url =basicUrl2+"devices/"+device_id;
			apikey =Constant.chqapikey;
		}
		String resultStr = OneNetHttpMethod.get(url,apikey);
		return resultStr;
//		JSONObject response = JSONObject.fromObject(resultStr);
//		return  (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
	}

	@Override
	public OneNetResponse delDevice(String device_id,String deviceType) {
		String apikey="";
		String url = "";
		if ("57".equals(deviceType)){
			url =basicUrl+"devices/"+device_id;
			apikey=Constant.gdapikey;
		} else if ("58".equals(deviceType)) {
			url =basicUrl2+"devices/"+device_id;
			apikey =Constant.chqapikey;
		}
//		String url = urlDevice+"/"+device_id;
		String resultStr = OneNetHttpMethod.delete(url,apikey);
		JSONObject response = JSONObject.fromObject(resultStr);
		return (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
	}

	@Override
	public String readDeviceResource(String device_id) {
		String url = basicUrl + "devices/"+device_id;
		String resultStr = OneNetHttpMethod.get(url);
		return resultStr;
	}

	@Override
	public OneNetResponse writeDeviceResource(String imei,String deviceType) {
		String apikey="";
		String url = "";
		String cmd = "";
		if ("57".equals(deviceType)){
			url =basicUrl+"nbiot";
			apikey=Constant.gdapikey;
		} else if ("58".equals(deviceType)) {
			url =basicUrl2+"nbiot";
			apikey =Constant.chqapikey;
			cmd = "2000140100050000012345020001000300010604000100";
		}
		
		ToolOneNetDao tond = new ToolOneNetDaoImpl();
		MsgOneNetEntity mone = tond.getMsgByImei(imei);
		String obj_id,obj_inst_id,res_id; // 必填
		String result ="";
		if (imei != null) {
			String[] ids = mone.getDs_id().split("_");
			obj_id = ids[0];
			obj_inst_id =  ids[1];
			res_id = ids[2];
			url +="?imei="+imei+"&obj_id="+obj_id+"&obj_inst_id="+obj_inst_id+"&mode=1";
			String jsonString = "{\"data\":[{\"res_id\":\""+res_id+"\",\"val\":\""+cmd+"\"}"+"]}";
			result = OneNetHttpMethod.postJson(url, jsonString,apikey);
		}
		JSONObject response = JSONObject.fromObject(result);
		 return  (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
	}

	//下发命令
	@Override
	public String sendCommand(String deviceId,String cmd,String deviceType) {
		String apikey="";
		String url = "";
		if ("57".equals(deviceType)){
			url =basicUrl+"nbiot/execute";
			apikey=Constant.gdapikey;
		} else if ("58".equals(deviceType)) {
			url =basicUrl2+"nbiot/execute";
			apikey =Constant.chqapikey;
			cmd = "2000140100050000012345020001000300010604000100";
		}
//		String url = urlCommand;
		ToolOneNetDao tond = new ToolOneNetDaoImpl();
		MsgOneNetEntity mone = tond.getMsgByDeviceId(deviceId);
		String imei,obj_id,obj_inst_id,res_id; // 必填
		imei =  mone.getImei();
		String result ="";
		if (imei != null) {
			String[] ids = mone.getDs_id().split("_");
			obj_id = ids[0];
			obj_inst_id =  ids[1];
			res_id = ids[2];
			url +="?imei="+imei+"&obj_id="+obj_id+"&obj_inst_id="+obj_inst_id+"&res_id="+res_id;
			String jsonString = "{\"args\":\""+cmd+"\"}";
			result = OneNetHttpMethod.postJson(url, jsonString,apikey);
		}
		return result;
	}
	
	@Override
	public OneNetResponse sendCommand(String imei,String deviceType) {
		String apikey="";
		String url = "";
		String cmd = "";
		if ("57".equals(deviceType)){
			url =basicUrl+"nbiot/execute";
			apikey=Constant.gdapikey;
		} else if ("58".equals(deviceType)) {
			url =basicUrl2+"nbiot/execute";
			apikey =Constant.chqapikey;
			cmd = "2000140100050000012345020001000300010604000100";
		}
//		String url = urlCommand;
		ToolOneNetDao tond = new ToolOneNetDaoImpl();
		MsgOneNetEntity mone = tond.getMsgByImei(imei);
		String obj_id,obj_inst_id,res_id; // 必填
		String result ="";
		if (imei != null) {
			String[] ids = mone.getDs_id().split("_");
			obj_id = ids[0];
			obj_inst_id =  ids[1];
			res_id = ids[2];
			url +="?imei="+imei+"&obj_id="+obj_id+"&obj_inst_id="+obj_inst_id+"&res_id="+res_id;
			String jsonString = "{\"args\":\""+cmd+"\"}";
			result = OneNetHttpMethod.postJson(url, jsonString,apikey);
		}
		JSONObject response = JSONObject.fromObject(result);
		 return  (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
	}

	//获取指定设备的资源列表，该接口经实测一直返回404
	@Override
	public String getAllResources(String imei) {
		String url = urlAllResources;
		Map<String,String> map = new HashMap<String, String>();
		map.put("imei", imei);
		String result = OneNetHttpMethod.getMap(url,map);
		return result;
	}

}
