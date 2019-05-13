package com.cloudfire.interceptor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

public class NorthqinHandler {
	public static String URL = "http://www.northqin.com/hanrun/connection/realtime/info";

	public static String post(JSONObject json) {

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(URL);

		post.setHeader("Content-Type", "application/json");
		post.addHeader("Authorization", "Basic YWRtaW46");
		String result = "";

		try {

			StringEntity s = new StringEntity(json.toString(), "utf-8");
			s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(s);

			// ��������
			HttpResponse httpResponse = client.execute(post);

			// ��ȡ��Ӧ������
			InputStream inStream = httpResponse.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inStream, "utf-8"));
			StringBuilder strber = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
				strber.append(line + "\n");
			inStream.close();

			result = strber.toString();
			System.out.println(result);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				System.out.println("����������ɹ�������Ӧ����");

			} else {

				System.out.println("��������ʧ��");

			}

		} catch (Exception e) {
			System.out.println("�����쳣");
			throw new RuntimeException(e);
		}

		return result;
	}

}
