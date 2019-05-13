package com.cloudfire.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudfire.dao.APIDao;
import com.cloudfire.dao.AreaDao;
import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.VideoDao;
import com.cloudfire.dao.impl.APIDaoImpl;
import com.cloudfire.dao.impl.AreaDaoImpl;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.VideoDaoImpl;
import com.cloudfire.entity.HttpRsult;
import com.cloudfire.push.TxtThread;
import com.cloudfire.until.OneNetHttpMethod;
import com.cloudfire.until.Utils;
import com.opensymphony.xwork2.ActionSupport;

public class VideoAlarmAction extends ActionSupport implements 
ServletRequestAware,ServletResponseAware{
	private static final long serialVersionUID = -6712397573973642166L;
	private HttpServletRequest req;
	private HttpServletResponse resp;
	
	@Override
	public void setServletResponse(HttpServletResponse resp) {
		this.resp = resp;
	}

	@Override
	public void setServletRequest(HttpServletRequest req) {
		this.req = req;
	}
	
	public void videoAlarm(){
//		oneNetDataBody:{"method":"OnEventNotify","params":{"ability":"event_rule","events":[{"data":{"channelID":1,"dataType":"behavioralAnalysis","dateTime":"2019-03-13 17:38:34","eventDescription":"fielddetection","eventType":"fielddetection","fielddetection":[{"targetAttrs":{"cameraIndexCode":"e615e5abbff4418090627cd080e7f067","channelName":"0301HK","deviceIndexCode":"32464aa4922646d88a08acb4804a01a0"}}],"ipAddress":"10.104.229.237","portNo":8080,"recvTime":"2019-03-13T17:38:18.141+08:00","sendTime":"2019-03-13T17:38:18.142+08:00"},"eventId":"639F006C-4DB4-4904-9C7F-AE3D4857619F","eventType":131588,"happenTime":"2019-03-13 17:38:34","srcIndex":"e615e5abbff4418090627cd080e7f067","srcName":"0301HK","srcType":"camera","status":0,"timeout":0}],"sendTime":"2019-03-13T17:38:18.142+08:00"}}
		try {
			BufferedReader br = null;
	        br = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
	        String line = null;
	        StringBuilder oneNetString = new StringBuilder();
	        while ((line = br.readLine()) != null) {
	        	oneNetString.append(line);
	        }
	        System.out.println("oneNetDataBody:"+oneNetString.toString());
			new Thread(new AlarmDataHandler(oneNetString.toString())).start();
			resp.getWriter().write("200");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	class AlarmDataHandler implements Runnable{
		private String data;
		
		AlarmDataHandler(String data){
			this.data = data;
		}
		
		@Override
		public void run() {
			try {
				handleAlarm(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private  void handleAlarm(String data) throws JSONException {
//		String data =  "{\"method\":\"OnEventNotify\",\"params\":{\"ability\":\"event_rule\",\"events\":[{\"data\":{\"channelID\":1,\"dataType\":\"behavioralAnalysis\",\"dateTime\":\"2019-03-13 17:38:34\",\"eventDescription\":\"fielddetection\",\"eventType\":\"fielddetection\",\"fielddetection\":[{\"targetAttrs\":{\"cameraIndexCode\":\"e615e5abbff4418090627cd080e7f067\",\"channelName\":\"0301HK\",\"deviceIndexCode\":\"32464aa4922646d88a08acb4804a01a0\"}}],\"ipAddress\":\"10.104.229.237\",\"portNo\":8080,\"recvTime\":\"2019-03-13T17:38:18.141+08:00\",\"sendTime\":\"2019-03-13T17:38:18.142+08:00\"},\"eventId\":\"639F006C-4DB4-4904-9C7F-AE3D4857619F\",\"eventType\":131588,\"happenTime\":\"2019-03-13 17:38:34\",\"srcIndex\":\"e615e5abbff4418090627cd080e7f067\",\"srcName\":\"0301HK\",\"srcType\":\"camera\",\"status\":0,\"timeout\":0}],\"sendTime\":\"2019-03-13T17:38:18.142+08:00\"}}";
		JSONObject obj1 = new JSONObject(data);
		JSONObject obj2 = new JSONObject(obj1.getString("params"));
		JSONArray obj3 = new JSONArray(obj2.getString("events"));
	    JSONObject obj4 = obj3.getJSONObject(0);
	    String eventType = obj4.getString("eventType");
	    String srcIndex = obj4.getString("srcIndex");
	    String srcName = obj4.getString("srcName");
//				Object events = new JSONObject(new JSONObject(data).get("params")).get("events");
		System.out.println(eventType+"->"+srcIndex+"->"+srcName); //事件类型+监控点编号+监控点名称
		
		VideoDao vd = new VideoDaoImpl();
		if (StringUtils.isNotBlank(srcIndex)){
			Map<String,String> videoInfo = vd.getVideoInfoByIndexCode(srcIndex);
			//推送视频弹窗
			FromRepeaterAlarmDao  mFromRepeaterAlarmDao =  new FromRepeaterAlarmDaoImpl();
			mFromRepeaterAlarmDao.addAlarmMsg(videoInfo.get("seqNum"), "", Integer.parseInt(eventType) , 1); 
			Utils.sendMessage(videoInfo.get("areaId"),"|0|");
			Utils.sendMessage(videoInfo.get("areaId"),srcIndex+"|5|"); 
			//短信电话推送
			GetPushUserIdDao mGetPushUserIdDao = new GetPushUserIdDaoImpl();
			List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(videoInfo.get("seqNum"));
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,videoInfo.get("seqNum")).start();        //短信通知的线程
			}
		}
	}
	
	public void addVideo(){
		String seqNum = req.getParameter("seqNum");
		String indexCode = req.getParameter("indexCode");
		String name = req.getParameter("name");
		String areaId = req.getParameter("areaId");
		
		VideoDao vd = new VideoDaoImpl();
		boolean result = vd.addVideoInfo(seqNum, indexCode, areaId, name);
		HttpRsult  hr = new HttpRsult();
		if (result)
			hr.setErrorCode(0);
		else 
			hr.setErrorCode(1);
		
		try {
			resp.getWriter().write(new JSONObject(hr).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getAllParentArea(){
		AreaDao ad = new AreaDaoImpl();
		Map<String,String> map = ad.getAllParentArea();
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getAreasByParentId(){
		AreaDao ad = new AreaDaoImpl();
		String parentId = req.getParameter("parentId");
		Map<String,String> map = ad.getAreasByParentId(parentId);
		
		this.resp.setContentType("text/html;charset=utf-8");
		this.resp.setCharacterEncoding("UTF-8");
		try {
			JSONObject result = new JSONObject(map);
			resp.getWriter().write(result.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String url = "http://localhost:8080/fireSystem/videoAlarm";
		String data =  "{\"method\":\"OnEventNotify\",\"params\":{\"ability\":\"event_rule\",\"events\":[{\"data\":{\"channelID\":1,\"dataType\":\"behavioralAnalysis\",\"dateTime\":\"2019-03-13 17:38:34\",\"eventDescription\":\"fielddetection\",\"eventType\":\"fielddetection\",\"fielddetection\":[{\"targetAttrs\":{\"cameraIndexCode\":\"e615e5abbff4418090627cd080e7f067\",\"channelName\":\"0301HK\",\"deviceIndexCode\":\"32464aa4922646d88a08acb4804a01a0\"}}],\"ipAddress\":\"10.104.229.237\",\"portNo\":8080,\"recvTime\":\"2019-03-13T17:38:18.141+08:00\",\"sendTime\":\"2019-03-13T17:38:18.142+08:00\"},\"eventId\":\"639F006C-4DB4-4904-9C7F-AE3D4857619F\",\"eventType\":131588,\"happenTime\":\"2019-03-13 17:38:34\",\"srcIndex\":\"e615e5abbff4418090627cd080e7f067\",\"srcName\":\"0301HK\",\"srcType\":\"camera\",\"status\":0,\"timeout\":0}],\"sendTime\":\"2019-03-13T17:38:18.142+08:00\"}}";
		String postJson = OneNetHttpMethod.postJson(url, data);
		System.out.println(postJson);
	}
	
	
}
