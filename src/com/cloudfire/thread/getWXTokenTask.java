package com.cloudfire.thread;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

import org.json.JSONObject;

import com.cloudfire.until.ByteArrayUtils;
import com.cloudfire.until.WXconstant;


public class getWXTokenTask extends TimerTask{

	@Override
	public void run() {
		InputStream inStream=null;
		try {
			String urlstring="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx314fd3193dea2861&secret=c375ba2acf7dc848a9b79879fe926b86";
			URL url=new URL(urlstring);
			HttpURLConnection connect=(HttpURLConnection)url.openConnection();
			connect.setRequestMethod("GET");
			
			connect.connect();
			
		
	        System.out.println(connect.getResponseCode()); // 响应代码 200表示成功
	        if (connect.getResponseCode() == 200) {
	            inStream = connect.getInputStream();
	            String result = new String(ByteArrayUtils.toByteArray(inStream), "UTF-8");
	            JSONObject respJsonObject=new JSONObject(result);
	            if(respJsonObject.has("access_token")){
	            	WXconstant.TOKEN=respJsonObject.getString("access_token");
	            }
	            System.out.println("WXTOKEN:"+WXconstant.TOKEN); // 响应代码 200表示成功
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
