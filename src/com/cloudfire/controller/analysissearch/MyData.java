package com.cloudfire.controller.analysissearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author cheng
 *2017-3-18
 *ÉÏÎç10:35:20
 */
public class MyData {
	public static String sendInfo(String strUrl,String requestMethod,String content) throws IOException{
    	StringBuffer buffer = new StringBuffer();
    	URL url = new URL(strUrl);
    	HttpURLConnection httpUrlConnection = (HttpURLConnection)url.openConnection();
    	httpUrlConnection.setRequestMethod(requestMethod);
    	httpUrlConnection.setRequestProperty("Content-type","application/json");
    	httpUrlConnection.setDoOutput(true); 
    	httpUrlConnection.setDoInput(true);
    	httpUrlConnection.setUseCaches(false); 
    	httpUrlConnection.setReadTimeout(30000);
    	httpUrlConnection.connect();
    	
        if(content.length()>0){
	        OutputStreamWriter out = new OutputStreamWriter(httpUrlConnection.getOutputStream(), "UTF-8");
		    out.write(content);
		    out.flush();
		    out.close(); 
        }
        
        BufferedReader in = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));   
        String inputLine;  
        while ((inputLine = in.readLine()) != null) {  
        	buffer.append(inputLine.trim());  
        }  
        in.close();
        httpUrlConnection.disconnect();
        return buffer.toString();
	}
}
