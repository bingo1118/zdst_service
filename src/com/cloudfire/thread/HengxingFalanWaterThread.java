package com.cloudfire.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.json.JSONObject;

import com.cloudfire.dao.FromRepeaterAlarmDao;
import com.cloudfire.dao.GetPushUserIdDao;
import com.cloudfire.dao.PublicUtils;
import com.cloudfire.dao.PushAlarmMsgDao;
import com.cloudfire.dao.WaterInfoDao;
import com.cloudfire.dao.impl.FromRepeaterAlarmDaoImpl;
import com.cloudfire.dao.impl.GetPushUserIdDaoImpl;
import com.cloudfire.dao.impl.PublicUtilsImpl;
import com.cloudfire.dao.impl.PushAlarmMsgDaoImpl;
import com.cloudfire.dao.impl.WaterInfoDaoImpl;
import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.push.MyThread;
import com.cloudfire.push.TxtThread;
import com.cloudfire.push.WebThread;
import com.cloudfire.until.ByteArrayUtils;

public class HengxingFalanWaterThread extends TimerTask{
	
	private WaterInfoDao mWaterInfoDao;
	private PublicUtils utilsDao;
	private FromRepeaterAlarmDao mFromRepeaterAlarmDao;
	private GetPushUserIdDao mGetPushUserIdDao;
	
	public static void main(String[] args) {
		HengxingFalanWaterThread falanWaterThread=new HengxingFalanWaterThread();
		falanWaterThread.run();
	}

	

	private void pushAlarm(String waterMac,String waterRepeaterMac,int waterAlarmType,int waterAlarmValue) {
		if(mFromRepeaterAlarmDao==null){
			mFromRepeaterAlarmDao=new FromRepeaterAlarmDaoImpl();
		}
		mFromRepeaterAlarmDao.addAlarmMsg(waterMac, waterRepeaterMac, waterAlarmType, waterAlarmValue);
		mWaterInfoDao = new WaterInfoDaoImpl();
		PushAlarmMsgDao mPushAlarmMsgDao = new PushAlarmMsgDaoImpl();
		PushAlarmMsgEntity pushAlarm = mPushAlarmMsgDao.getPushAlarmMsg(waterMac, waterAlarmType);
		pushAlarm.setAlarmFamily(waterAlarmValue+"");
		pushAlarm.setAlarmType(waterAlarmType+"");
		mGetPushUserIdDao = new GetPushUserIdDaoImpl();
		List<String> users = mGetPushUserIdDao.getAllUser(waterMac);
		List<String> txtUserList = mGetPushUserIdDao.getPushUserIdByMac(waterMac); //获取useridsmoke表里的用户用于短信通知
		if(users!=null&&users.size()>0){
			Map<String,String> iosMap = mGetPushUserIdDao.getIosUser(users);
			new MyThread(pushAlarm,users,iosMap).start();
			new WebThread(users,waterMac).start();
			if (txtUserList != null &&txtUserList.size()>0) {
				new TxtThread(txtUserList,waterMac).start();        //短信通知的线程
			}
		}
	}



	@Override
	public void run() {
		InputStream inStream=null;
		try {
			String urlstring="http://119.23.104.41:8888/fireplug/realDataList"+"?tokenId=7d90a90752c84a85b8d2d32970370121";
			URL url=new URL(urlstring);
			HttpURLConnection connect=(HttpURLConnection)url.openConnection();
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			
	        if (connect.getResponseCode() == 200) {
	            inStream = connect.getInputStream();
	            String result = new String(ByteArrayUtils.toByteArray(inStream), "UTF-8");
	            System.out.println("HENGXING:"+result); // 响应代码 200表示成功
	            JSONObject object=new JSONObject(result);
	            String errcode=object.getString("status");
	            if(errcode.equals("success")){
	            	org.json.JSONArray array=object.getJSONArray("dataList");
	            	for(int i=0;i<array.length();i++){
	            		JSONObject obj=array.getJSONObject(i);
	            		if(mWaterInfoDao==null){
		            		mWaterInfoDao = new WaterInfoDaoImpl();
	            		}
	            		String waterMac=obj.getString("sheBeiId");
	            		int waterStatus=obj.getInt("shuiYaIdx");//水压状态  0 正常  1欠压
	            		double waterValue=obj.getDouble("shuiYa");
	            		long getDataTime=obj.getLong("createTime");
	            		if(waterStatus==1&&(System.currentTimeMillis()-getDataTime)<1000*60*60){
	            			waterStatus=209;
	            			pushAlarm(waterMac,"",waterStatus,(int)(waterValue*1000));
	            		}
	            		
	            		
	            		int state=obj.getInt("sheBeiZtIdx");// 设备状态   0 正常  1警告  2离线  3其他
	            		if(utilsDao==null){
		            		utilsDao = new PublicUtilsImpl();
	            		}
	            		if(state==2){
							utilsDao.updateDeviceOnlineState(waterMac,0,getDataTime);
	            		}else{
	            			utilsDao.updateDeviceOnlineState(waterMac,1,getDataTime);
	            		}
						mWaterInfoDao.addHengxingFalanWaterInfo("", waterMac, waterStatus, (waterValue*1000)+"",getDataTime);
	            	}
	            }
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(inStream!=null){
				try {
					inStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
