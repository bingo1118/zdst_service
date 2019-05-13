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
 * 微信支付
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
			p.put("error", "订单号为空");
		}
		//生成订单 XML格式
		String orderInfo = createOrderInfo(out_trade_no,total_fee);
		
		//统一下单api
		String  code_url = httpOrder(orderInfo);
		
		ServletOutputStream sos = null;
		try {
			sos = resp.getOutputStream();
			QRCodeUtil.encode(code_url, sos);//调用生成二维码的方法
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
			QRCodeUtil.encode("朱中华最帅", sos);
		} catch (Exception e) {
			e.printStackTrace();
		}//调用生成二维码的方法
	}
	/**
     * 生成统一下单格式的订单，生成一个XML格式的字符串
     * @param orderId
     * @return
     */
	private String createOrderInfo(String orderid,String total_fee) throws UnsupportedEncodingException{
		//生成预付单对象
		PayOrder payOrder = new PayOrder();
		//生成随机字符串
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
		//获得签名
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
     * 调统一下单API
     * @param orderInfo
     * @return
     */
	 private String httpOrder(String orderInfo) {
	        String url = WeChatConfig.PLACEANORDER_URL;
	        try {
	            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	            //加入数据
	            conn.setRequestMethod("POST");
	            conn.setDoOutput(true);

	            BufferedOutputStream buffOutStr = new BufferedOutputStream(conn.getOutputStream());
	            buffOutStr.write(orderInfo.getBytes("UTF-8"));
	            buffOutStr.flush();
	            buffOutStr.close();

	            //获取输入流
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

	            String line = null;
	            StringBuffer sb = new StringBuffer();
	            while ((line = reader.readLine()) != null) {
	                sb.append(line);
	            }
	            Map<String, String> map = WXPayUtil.xmlToMap(sb.toString());
	            //返回字段很多，这里只取我们所需的字段
	            String return_msg = map.get("return_msg");//返回信息
	            String return_code = map.get("return_code");//状态码
	            String result_code = map.get("result_code");//业务结果
	            String code_url = map.get("code_url");
	            //根据微信文档return_code 和result_code都为SUCCESS的时候才会返回code_url
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
	     * 微信回调函数
	     * 支付成功后微信服务器会调用此方法，修改数据库订单状态
	     */
	    @RequestMapping(value = "/wxPayCallBack.do")
	    public String wxPayCallBack(HttpServletRequest request, HttpServletResponse response) {
	        System.out.println("回调成功");
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
	            String result = new String(outSteam.toByteArray(), "utf-8");// 获取微信调用我们notify_url的返回信息
	            Map<String, String> map = WXPayUtil.xmlToMap(result);
	            if (map.get("result_code").equalsIgnoreCase("SUCCESS")) {
	                //返回成功后修改订单状态
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
     * 生成签名
     * 这个方法是从微信sdk里copy过来的，自己也可以写，要注意生成签名后UTF-8的转换，要不然容易报签名Body UTF-8错误
     * @param data 待签名数据
     * @param key  API密钥
     */
    public static String createSign(final Map<String, String> data, String key) throws Exception {
        return createSign(data, key, SignType.MD5);
    }
    
    
    /**
     * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
     *
     * @param data     待签名数据
     * @param key      API密钥
     * @param signType 签名方式
     * @return 签名
     */
    private static String createSign(final Map<String, String> data, String key, SignType signType) throws Exception {
        //根据规则创建可排序的map集合
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (k.equals(WXPayConstants.FIELD_SIGN)) {
                continue;
            }
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("key=").append(key);
        //转换UTF-8
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
