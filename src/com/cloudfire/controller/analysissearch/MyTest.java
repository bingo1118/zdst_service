/**
 * ионГ11:55:24
 */
package com.cloudfire.controller.analysissearch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author cheng
 *2017-3-18
 *ионГ11:55:24
 */
public class MyTest {
	public void test1() throws Exception{
		String strURL = "http://www.anjismart.com:7770/QueryCodeService.asmx/QueryCode?code=425144174493800&aid=302";
        URL url = new URL(strURL);
        HttpURLConnection httpConn = (HttpURLConnection)
        url.openConnection();
        httpConn.setRequestMethod("GET");
        httpConn.connect();
            
        BufferedReader reader = new BufferedReader(new InputStreamReader(
        httpConn.getInputStream()));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
        	buffer.append(line);
        }
        reader.close();
        httpConn.disconnect();
	}
}
