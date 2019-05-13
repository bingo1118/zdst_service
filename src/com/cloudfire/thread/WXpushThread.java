package com.cloudfire.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.cloudfire.entity.PushAlarmMsgEntity;
import com.cloudfire.entity.WXentity.AlarmMedol;
import com.cloudfire.entity.WXentity.Data;
import com.cloudfire.entity.WXentity.DataInfo;
import com.cloudfire.until.ByteArrayUtils;
import com.cloudfire.until.WXconstant;


public class WXpushThread implements Runnable{
	
	String userid;
	PushAlarmMsgEntity content;
	String alarmtype="火警";
	private int regetTokenTime=0;

	public WXpushThread(String userid, PushAlarmMsgEntity content,
			String alarmtype) {
		super();
		this.userid = userid;
		this.content = content;
		this.alarmtype = alarmtype;
	}

	public WXpushThread(String userid,PushAlarmMsgEntity content) {
		super();
		this.userid = userid;
		this.content=content;
	}

	@Override
	public void run() {
		InputStream inStream=null;
		try {
			String urlstring=WXconstant.WX_SERVICE_IP2+"?access_token="+WXconstant.TOKEN;
			URL url=new URL(urlstring);
			HttpURLConnection connect=(HttpURLConnection)url.openConnection();
			connect.setRequestMethod("POST");
			connect.setDoOutput(true);
			OutputStream outStream = connect.getOutputStream();
			
			AlarmMedol alarmMedol=new AlarmMedol();
			alarmMedol.setTouser(userid);
			System.out.println("WXPUSHopenid:"+userid);
			Data data=new Data();
			data.setFirst(new DataInfo("设备报警"));
			data.setKeyword1(new DataInfo(content.getName()));
			data.setKeyword2(new DataInfo(content.getMac()));
			data.setKeyword3(new DataInfo(content.getAlarmTime()));
			data.setKeyword4(new DataInfo(content.getAddress()));
			data.setKeyword5(new DataInfo(this.alarmtype));
			data.setRemark(new DataInfo("请及时查看报警信息！"));
			alarmMedol.setData(data);
			
			JSONObject jsonObject=new JSONObject(alarmMedol);
			
			System.out.println(jsonObject.toString());
	        outStream.write(jsonObject.toString().getBytes("UTF-8"));
	        outStream.flush();
	        outStream.close();
	        if (connect.getResponseCode() == 200) {
	            inStream = connect.getInputStream();
	            String result = new String(ByteArrayUtils.toByteArray(inStream), "UTF-8");
	            System.out.println("WXPUSH:"+result); // 响应代码 200表示成功
	            JSONObject object=new JSONObject(result);
	            int errcode=object.getInt("errcode");
	            switch (errcode) {
					case 0:
						System.out.println("WXPUSH:SUCCESS");
						break;
					case 41001:
					case 40001:
						if(regetTokenTime<3){
							new getWXTokenTask().run();
							System.out.println("WXPUSH:regetToken");
							this.run();
							regetTokenTime++;
						}
						break;
					default:
						break;
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
