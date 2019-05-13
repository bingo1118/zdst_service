package com.cloudfire.until;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;


public class OneNetHttpMethod {
	
	/**
     * get请求，参数拼接在地址上
     * @param url 请求地址加参数
     * @return 响应
     */
    public static String get(String url)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            get.setHeader("Content-Type","application/json");
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * get请求，参数放在map里
     * @param url 请求地址
     * @param map 参数map
     * @return 响应
     */
    public  static String getMap(String url,Map<String,String> map)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//        for(Map.Entry<String,String> entry : map.entrySet())
//        {
//            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//        }
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
            Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				builder.addParameter(key, map.get(key));
			}
//            builder.setParameters(pairs);
            HttpGet get = new HttpGet(builder.build());
            get.setHeader("Content-Type","application/json");
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 访问出其他服务器来查看主机设备状态
     * @param url
     * @param map
     * @return findRepeater为主机在线，notFindRepeater为主机离线状态
     */
    public  static boolean getRepeaterUrl(String url,Map<String,String> map)
    {
    	boolean bool=false;
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//        for(Map.Entry<String,String> entry : map.entrySet())
//        {
//            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//        }
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
//            builder.setParameters(pairs);
            Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				builder.addParameter(key, map.get(key));
			}
            HttpGet get = new HttpGet(builder.build());
            get.setHeader("Content-Type","application/json");
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
                if(result.equals("findRepeater")){
                	bool=true;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bool;
    }
    
    
    /**
     * 发送post请求，参数用map接收
     * @param url 地址
     * @param map 参数
     * @return 返回值
     */
    public static  String postMap(String url,Map<String,String> map) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("Content-Type","application/json");
            post.setEntity(new UrlEncodedFormEntity( pairs,"UTF-8"));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
    
    /**
     * 发送post请求，参数用map接收
     * @param url 地址
     * @param map 参数
     * @return 返回值
     */
    public static  String postMapInForm(String url,Map<String,String> map) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("Content-Type","application/x-www-form-urlencoded");
            post.setEntity(new UrlEncodedFormEntity( pairs,"UTF-8"));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
    
    /**
     * 发送post请求，参数用map接收
     * @param url 地址
     * @param map 参数
     * @return 返回值
     */
    public static  String postMapInForm2(String url,Map<String,String> map,String tokenId) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("Content-Type","application/x-www-form-urlencoded");
        	post.setHeader("tokenId", tokenId);
            post.setEntity(new UrlEncodedFormEntity( pairs,"UTF-8"));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
    
    /**
     * post请求，参数为json字符串
     * @param url 请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public static String postJson(String url,String jsonString)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("Content-Type","application/json");
            post.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * delete请求，
     * @param url 请求地址
     * @return 响应
     */
    public static String delete(String url)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(url);
        CloseableHttpResponse response = null;
        try {
            delete.setHeader("Content-Type","application/json");
            response = httpClient.execute(delete);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * put请求，参数为json字符串
     * @param url 请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public static String putJson(String url,String jsonString)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut(url);
        CloseableHttpResponse response = null;
        try {
        	put.setHeader("Content-Type","application/json");
        	put.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            response = httpClient.execute(put);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static String entityToString(HttpEntity entity) throws IOException {
        String result = null;
        if(entity != null)
        {
            long lenth = entity.getContentLength();
            if(lenth != -1 && lenth < 2048)
            {
                result = EntityUtils.toString(entity,"UTF-8");
            }else {
                InputStreamReader reader1 = new InputStreamReader(entity.getContent(), "UTF-8");
                CharArrayBuffer buffer = new CharArrayBuffer(2048);
                char[] tmp = new char[1024];
                int l;
                while((l = reader1.read(tmp)) != -1) {
                    buffer.append(tmp, 0, l);
                }
                result = buffer.toString();
            }
        }
        return result;
    }
    
    /**
     * get请求，参数拼接在地址上
     * @param url 请求地址加参数
     * @return 响应
     */
    public static String get(String url,String apikey)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            get.setHeader("api-key", apikey);
            get.setHeader("Content-Type","application/json");
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * get请求，参数放在map里
     * @param url 请求地址
     * @param map 参数map
     * @return 响应
     */
    public  static String getMap(String url,Map<String,String> map,String apikey)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//        for(Map.Entry<String,String> entry : map.entrySet())
//        {
//            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//        }
        CloseableHttpResponse response = null;
        try {
            URIBuilder builder = new URIBuilder(url);
//            builder.setParameters(pairs);
            Iterator<String> iterator = map.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				builder.addParameter(key, map.get(key));
			}
            HttpGet get = new HttpGet(builder.build());
            get.setHeader("api-key", apikey);
            get.setHeader("Content-Type","application/json");
            response = httpClient.execute(get);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    
    /**
     * 发送post请求，参数用map接收
     * @param url 地址
     * @param map 参数
     * @return 返回值
     */
    public static  String postMap(String url,Map<String,String> map,String apikey) {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for(Map.Entry<String,String> entry : map.entrySet())
        {
            pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
        }
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("api-key", apikey);
        	post.setHeader("Content-Type","application/json");
            post.setEntity(new UrlEncodedFormEntity( pairs,"UTF-8"));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }
    
    /**
     * post请求，参数为json字符串
     * @param url 请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public static String postJson(String url,String jsonString,String apikey)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
        	post.setHeader("api-key", apikey);
        	post.setHeader("Content-Type","application/json");
            post.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            response = httpClient.execute(post);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * delete请求，
     * @param url 请求地址
     * @return 响应
     */
    public static String delete(String url,String apikey)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(url);
        CloseableHttpResponse response = null;
        try {
            delete.setHeader("api-key", apikey);
            delete.setHeader("Content-Type","application/json");
            response = httpClient.execute(delete);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * put请求，参数为json字符串
     * @param url 请求地址
     * @param jsonString json字符串
     * @return 响应
     */
    public static String putJson(String url,String jsonString,String apikey)
    {
        String result = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut put = new HttpPut(url);
        CloseableHttpResponse response = null;
        try {
        	put.setHeader("api-key", "S60Brswndrcu3wmNMY8hVyhGOzE=");
        	put.setHeader("Content-Type","application/json");
        	put.setEntity(new ByteArrayEntity(jsonString.getBytes("UTF-8")));
            response = httpClient.execute(put);
            if(response != null && response.getStatusLine().getStatusCode() == 200)
            {
                HttpEntity entity = response.getEntity();
                result = entityToString(entity);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                httpClient.close();
                if(response != null)
                {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public static void main(String[] args) {
		String json= "{\"notifyType\":\"deviceDataChanged\",\"deviceId\":\"36fbd204-d022-4db8-bc6a-de84e23bf7cc\",\"gatewayId\":\"36fbd204-d022-4db8-bc6a-de84e23bf7cc\",\"requestId\":null,\"service\":{\"serviceId\":\"stDevID\",\"serviceType\":\"stDevID\",\"data\":{\"stDevID_T\":1,\"stDevID_L\":5,\"stDevID_HV\":4207202,\"stDevID_LV\":29254},\"eventTime\":\"20181106T014409Z\"}}";
		String url = "http://193.112.215.201:51091/fireSystem/nanjing-report-dev-callback";
		String postJson = postJson(url, json);
		System.out.println(postJson+"\n"+GetTime.ConvertTimeByLong());
	}
    
}
