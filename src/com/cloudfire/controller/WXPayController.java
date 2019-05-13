package com.cloudfire.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cloudfire.entity.wxpay.PayOrder;
import com.cloudfire.until.WriteJson;
import com.cloudfire.until.WxPayUtil.QRCodeUtil;
import com.cloudfire.until.WxPayUtil.WeChatConfig;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayConstants.SignType;
import com.github.wxpay.sdk.WXPayUtil;
/**
 * ΢��֧��
 * @author Administrator
 *
 */
@Controller
public class WXPayController {
	private final static Log log = LogFactory.getLog(WXPayController.class);
	
	
	@RequestMapping(value="wxpay.do")
	public Map createQRCode(HttpServletRequest req,HttpServletResponse resp) throws UnsupportedEncodingException{
		String out_trade_no = req.getParameter("orderId");
		String total_fee = req.getParameter("orderMoney");
		Map<String, String> p = new HashMap<>();
		
		if(out_trade_no.isEmpty()){
			p.put("error", "������Ϊ��");
		}
		//���ɶ��� XML��ʽ
		String orderInfo = createOrderInfo(out_trade_no,total_fee);
		
		//ͳһ�µ�api
		String  code_url = httpOrder(orderInfo);
		
		ServletOutputStream sos = null;
		try {
			sos = resp.getOutputStream();
			QRCodeUtil.encode(code_url, sos);//�������ɶ�ά��ķ���
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return p;

	}
	@RequestMapping(value="testTo.do")
	public void testTo(HttpServletResponse resp) throws IOException{
		ServletOutputStream sos = null;
		try {
			sos = resp.getOutputStream();
			QRCodeUtil.encode("���л���˧", sos);
		} catch (Exception e) {
			e.printStackTrace();
		}//�������ɶ�ά��ķ���
	}
	/**
     * ����ͳһ�µ���ʽ�Ķ���������һ��XML��ʽ���ַ���
     * @param orderId
     * @return
     */
	private String createOrderInfo(String orderid,String total_fee) throws UnsupportedEncodingException{
		//����Ԥ��������
		PayOrder payOrder = new PayOrder();
		//��������ַ���
		String nonce_str = UUID.randomUUID().toString().trim().replaceAll("-", "");
		payOrder.setAppid(WeChatConfig.APPID);
		payOrder.setBody("1");
		payOrder.setMch_id(WeChatConfig.MCH_ID);
		payOrder.setNotify_url(WeChatConfig.NOTIFY_URL);
		payOrder.setOut_trade_no(orderid);
		payOrder.setTotal_fee(total_fee);
		payOrder.setNonce_str(nonce_str);
		payOrder.setTrade_type(WeChatConfig.TRADE_TYPE);
		payOrder.setSpbill_create_ip(WeChatConfig.SPBILL_CREATE_IP);
		Map<String, String> p = new HashMap<>();
		p.put("appid", WeChatConfig.APPID);
		p.put("mch_id", WeChatConfig.MCH_ID);
		p.put("body", "1");
		p.put("nonce_str", nonce_str);
		p.put("out_trade_on", orderid);
		p.put("spbill_create_ip", WeChatConfig.SPBILL_CREATE_IP);
		p.put("notify_url", WeChatConfig.NOTIFY_URL);
		p.put("trade_type", WeChatConfig.TRADE_TYPE);
		//���ǩ��
		String sign = null;
		try {
			sign = createSign(p,WeChatConfig.KEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.put(sign, sign);
		String s = null;
		try {
			s = WXPayUtil.mapToXml(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new String(s.getBytes("UTF-8"));
	}
	
	/**
     * ��ͳһ�µ�API
     * @param orderInfo
     * @return
     */
	 private String httpOrder(String orderInfo) {
	        String url = WeChatConfig.PLACEANORDER_URL;
	        try {
	            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	            //��������
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);

	            BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
	            buffOutStr.write(orderInfo.getBytes("UTF-8"));
	            buffOutStr.flush();
	            buffOutStr.close();

	            //��ȡ������
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

	            String line = null;
	            StringBuffer sb = new StringBuffer();
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            Map<String, String> map = WXPayUtil.xmlToMap(sb.toString());
	            //�����ֶκܶ࣬����ֻȡ����������ֶ�
	            String return_msg = map.get("return_msg");//������Ϣ
	            String return_code = map.get("return_code");//״̬��
	            String result_code = map.get("result_code");//ҵ����
	            String code_url = map.get("code_url");
	            //����΢���ĵ�return_code ��result_code��ΪSUCCESS��ʱ��Ż᷵��code_url
	            if (null != map && "SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
	                return code_url;
	            } else {
	                return null;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	 
	 
	 /**
	     * ΢�Żص�����
	     * ֧���ɹ���΢�ŷ���������ô˷������޸����ݿⶩ��״̬
	     */
	    @RequestMapping(value = "/wxPayCallBack.do")
	    public String wxPayCallBack(HttpServletRequest request, HttpServletResponse response) {
	        System.out.println("�ص��ɹ�");
	        try {
	            InputStream inStream = request.getInputStream();
	            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	            byte[] buffer = new byte[1024];
	            int len = 0;
	            while ((len = inStream.read(buffer)) != -1) {
	                outSteam.write(buffer, 0, len);
	            }
	            outSteam.close();
	            inStream.close();
	            String result = new String(outSteam.toByteArray(), "utf-8");// ��ȡ΢�ŵ�������notify_url�ķ�����Ϣ
	            Map<String, String> map = WXPayUtil.xmlToMap(result);
	            if (map.get("result_code").equalsIgnoreCase("SUCCESS")) {
	                //���سɹ����޸Ķ���״̬
	                String out_trade_no = map.get("out_trade_no");
	               // this.proOrdersService.updateByOrderId(out_trade_no);
	            }
	        } catch (Exception e) {

	        }
	        try {
				response.getWriter().write("SUCCESS");
			} catch (IOException e) {
				e.printStackTrace();
			}
	        return "SUCCESS";
	    }
	
	
	 /**
     * ����ǩ��
     * ��������Ǵ�΢��sdk��copy�����ģ��Լ�Ҳ����д��Ҫע������ǩ����UTF-8��ת����Ҫ��Ȼ���ױ�ǩ��Body UTF-8����
     * @param data ��ǩ������
     * @param key  API��Կ
     */
    public static String createSign(final Map<String, String> data, String key) throws Exception {
        return createSign(data, key, SignType.MD5);
    }
    
    
    /**
     * ����ǩ��. ע�⣬������sign_type�ֶΣ������signType��������һ�¡�
     *
     * @param data     ��ǩ������
     * @param key      API��Կ
     * @param signType ǩ����ʽ
     * @return ǩ��
     */
    private static String createSign(final Map<String, String> data, String key, SignType signType) throws Exception {
        //���ݹ��򴴽��������map����
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WXPayConstants.FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // ����ֵΪ�գ��򲻲���ǩ��
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        //ת��UTF-8
        String str = new String(sb.toString().getBytes("UTF-8"));
        if (WXPayConstants.SignType.MD5.equals(signType)) {
            return WXPayUtil.MD5(sb.toString()).toUpperCase();
        } else if (WXPayConstants.SignType.HMACSHA256.equals(signType)) {
            return WXPayUtil.HMACSHA256(sb.toString(), key);
        } else {
            throw new Exception(String.format("Invalid sign_type: %s", signType));
        }
    }
}
