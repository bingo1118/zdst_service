package com.cloudfire.until;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MyUtils {
	/*
	 * 鏄惁鏄函鏁板瓧瀛椾覆
	 */
	public static boolean isNumeric(String str) {
		if (null == str) {
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static String convertPlanTime(int time) {
		int minute_to = (time & 0xff);
		int minute_from = ((time >> 8) & 0xff);
		int hour_to = ((time >> 16) & 0xff);
		int hour_from = ((time >> 24) & 0xff);
		StringBuilder sb = new StringBuilder();
		if (hour_from < 10) {
			sb.append("0" + hour_from + ":");
		} else {
			sb.append(hour_from + ":");
		}
		if (minute_from < 10) {
			sb.append("0" + minute_from + "-");
		} else {
			sb.append(minute_from + "-");
		}

		if (hour_to < 10) {
			sb.append("0" + hour_to + ":");
		} else {
			sb.append(hour_to + ":");
		}

		if (minute_to < 10) {
			sb.append("0" + minute_to);
		} else {
			sb.append("" + minute_to);
		}
		return sb.toString();
	}

	public static int convertPlanTime(String time) {
		int iRet = 0;
		try {
			String[] times = time.split("-");
			String[] time_from = times[0].split(":");
			String[] time_to = times[1].split(":");

			iRet = ((Integer.parseInt(time_from[0]) << 24)
					| (Integer.parseInt(time_to[0]) << 16)
					| (Integer.parseInt(time_from[1]) << 8) | (Integer
					.parseInt(time_to[1]) << 0));
			return iRet;
		} catch (Exception e) {
			return iRet;
		}

	}

	public static String convertDeviceTime(int iTime) {
		int year = (2000 + ((iTime >> 24)));
		int month = (iTime >> 18) & 0x3f;
		int day = (iTime >> 12) & 0x3f;
		int hour = (iTime >> 6) & 0x3f;
		int minute = (iTime >> 0) & 0x3f;

		StringBuilder sb = new StringBuilder();
		sb.append(year + "-");

		if (month < 10) {
			sb.append("0" + month + "-");
		} else {
			sb.append(month + "-");
		}

		if (day < 10) {
			sb.append("0" + day + " ");
		} else {
			sb.append(day + " ");
		}

		if (hour < 10) {
			sb.append("0" + hour + ":");
		} else {
			sb.append(hour + ":");
		}

		if (minute < 10) {
			sb.append("0" + minute);
		} else {
			sb.append("" + minute);
		}
		return sb.toString();
	}

	public static long convertTimeStringToInterval(String time) {
		long interval = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date d;
		try {

			d = sdf.parse(time);
			interval = d.getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return interval;
	}
	
	public static void main(String[] args) {
		System.out.println(getBitProcessingVersion());
	}
	
	public static String getBitProcessingVersion() {
		try {
			String[] parseVerson = getVersion().split("\\.");
			int a = Integer.parseInt(parseVerson[0]) << 24;
			int b = Integer.parseInt(parseVerson[1]) << 16;
			int c = Integer.parseInt(parseVerson[2]) << 8;
			int d = Integer.parseInt(parseVerson[3]);
			return String.valueOf((a | b | c | d));
		} catch (Exception e) {
			return "9999";
		}
	}

	public static String getVersion() {
		String version = "";
		InputStream input = MyUtils.class.getClassLoader().getResourceAsStream(
				"version.xml");
		try {
			HashMap<String, String> data = parseXml(input);
			// 鑾峰彇褰撳墠鐗堟湰淇℃伅
			version = data.get("version");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (version.equals("")) {
			return "00.46.00.07";
		}
		return version;
	}

	/*
	 * 璇诲彇version.xml鐗堟湰淇℃伅
	 */
	private static HashMap<String, String> parseXml(InputStream input)
			throws Exception {
		HashMap<String, String> hashMap = new HashMap<String, String>();

		// 瀹炰緥鍖栦竴涓枃妗ｆ瀯寤哄櫒宸ュ巶
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// 閫氳繃鏂囨。鏋勫缓鍣ㄥ伐鍘傝幏鍙栦竴涓枃妗ｆ瀯寤哄櫒
		DocumentBuilder builder = factory.newDocumentBuilder();
		// 閫氳繃鏂囨。閫氳繃鏂囨。鏋勫缓鍣ㄦ瀯寤轰竴涓枃妗ｅ疄渚�
		Document document = builder.parse(input);
		// 鑾峰彇XML鏂囦欢鏍硅妭鐐�
		Element root = document.getDocumentElement();
		// 鑾峰緱鎵�湁瀛愯妭鐐�
		NodeList childNodes = root.getChildNodes();
		for (int j = 0; j < childNodes.getLength(); j++) {
			// 閬嶅巻瀛愯妭鐐�
			Node childNode = (Node) childNodes.item(j);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
//				Element childElement = (Element) childNode;
				// 鐗堟湰鍙�
				if ("version".equals(childNode.getNodeName())) {
					hashMap.put("version", childNode.getFirstChild()
							.getNodeValue());
				}
			}
		}
		return hashMap;
	}

	/**
	 * 灏唅nt鏁板�杞崲涓哄崰鍥涗釜瀛楄妭鐨刡yte鏁扮粍锛屾湰鏂规硶閫傜敤浜�楂樹綅鍦ㄥ墠锛屼綆浣嶅湪鍚�鐨勯『搴�
	 */
	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) (value & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[0] = (byte) ((value >> 24) & 0xFF);

		return src;
	}

	public static byte[] btop = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01, 0x00,
			0x08, 0x00, (byte) 0xff, 0x08 };// 涓�
	public static byte[] bbottom = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x00, 0x10, 0x00, (byte) 0xff, 0x10 };// 涓�
	public static byte[] bleft = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x00, 0x04, (byte) 0xff, 0x00, 0x04 };// 宸�
	public static byte[] bright = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x00, 0x02, (byte) 0xff, 0x00, 0x02, };// 鍙�

	public static byte[] zoom_small = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x00, 0x20, 0x00, 0x00, 0x21 };// 鍙樺�鐭�
	public static byte[] zoom_big = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x00, 0x40, 0x00, 0x00, 0x41 };// 鍙樺�闀�
	public static byte[] focus_small = { (byte) 130, 1, 7, 0, (byte) 0xff,
			0x01, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0x81 };// 鑱氱劍杩�
	public static byte[] focus_big = { (byte) 130, 1, 7, 0, (byte) 0xff, 0x01,
			0x01, 0x00, 0x00, 0x00, 0x02 };// 鑱氱劍杩�
	public static byte[] aperture_smal = { (byte) 130, 1, 7, 0, (byte) 0xff,
			0x01, 0x02, 0x00, 0x00, 0x00, 0x03 };// 鍏夊湀灏�
	public static byte[] aperture_big = { (byte) 130, 1, 7, 0, (byte) 0xff,
			0x01, 0x04, 0x00, 0x00, 0x00, 0x05 };// 鍏夊湀澶�

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
		return value;
	}
	/**
	 * 涓存椂鏍煎紡鍖栨暟瀛�
	 * @param src
	 * @param len
	 * @return
	 */
	public static String compleInteger(int src, int len) {

		if (src < 10) {
			return "0" + src;
		} else {
			return String.valueOf(src);
		}

	}
	
	
	public static void render(HttpServletResponse response, String contextType, String text) {

		response.setContentType(contextType);

		try {
			response.getWriter().write(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void renderJson(HttpServletResponse response, String text) {
		String contextType = "application/json;charset=utf-8";

		render(response, contextType, text);

	}
	
	//根据List<String> areaIds 的值返回一个字符串
	public static String getStringByList(List<String> areaIds){
		StringBuffer sb = new StringBuffer();
		if (areaIds != null && areaIds.size() > 0) {
			sb.append("(");
			int len = areaIds.size();
			if (len > 1){
				for (int i = 0; i < len - 1; i++) {
					sb.append(areaIds.get(i));
					sb.append(",");
				}
			}
			sb.append(areaIds.get(len - 1)); //加上list的最后一个元素
			sb.append(")");
		} else {
			sb.append("(0)");
		}
		return sb.toString();
	}

}
