package com.cloudfire.until;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

//import com.alibaba.fastjson.JSON;
//import com.cloudfire.entity.HK_response;
//import com.hikvision.artemis.sdk.ArtemisHttpUtil;
//import com.hikvision.artemis.sdk.config.ArtemisConfig;

public class HKUtil {
//	static {
//		ArtemisConfig.host = "111.230.205.112:9016";//"112.65.140.140:1443"; // artemis���ط�����ip�˿�
//		ArtemisConfig.appKey = "21836658"; //23565114;//"20908044"; // ��Կappkey
//		ArtemisConfig.appSecret = "wKJEkJ8cm8260ThW9JcQ";//"2XkWa4s0Z4R0X4XbST3u";//"J5kNJ08zxdqImAl8eC2q";// ��ԿappSecret
//	}
//	
//	/**
//	 * ��������ƽ̨����վ·��
//	 * TODO ·�������޸ģ�����/artemis
//	 */
//	private static final String ARTEMIS_PATH = "/artemis";
//	
//	public static String postApi(String url,Map<String,String> map){
//		String getCamsApi = ARTEMIS_PATH + url;
//		String body = JSON.toJSON(map).toString();
//		Map<String, String> path = new HashMap<String, String>();
//		path.put("http://", getCamsApi);
//		return  ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
//	}
//	
//	/**
//	 * ����POST�������ͽӿڣ������Ի�ȡ��֯�б�Ϊ��
//	 * https://ip:port/artemis/api/resource/v1/org/orgList
//	 */
//	public static String callPostApiGetOrgList() {
//		/**
//		 * https://ip:port/artemis/api/resource/v1/org/orgList
//		 * ����API�ĵ����Կ�����,����һ��POST�����Rest�ӿ�, ���Ҵ���Ĳ���ΪJSON�ַ���.
//		 * ArtemisHttpUtil�������ṩ��doPostFormArtemis�������, һ������������ĵ���д�����е���˼. ��Ϊ�ӿ���https,
//		 * ���Ե�һ������path�Ǹ�hashmap����,��putһ��key-value, querysΪ����Ĳ���. 
//		 * body ΪJSON�ַ���.
//		 * query������,���Դ���null,accept��contentType��ָ������Ĭ�ϴ�null.
//		 */
//		String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/org/orgList";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("cameraIndexCode","4a10a0852d8c49e6a040df7e000bd87f");
//		paramMap.put("protocol","hls");
//		String body = JSON.toJSON(paramMap).toString();
//		Map<String, String> path = new HashMap<String, String>();
//		path.put("https://", getCamsApi);
//		String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
//		return result;
//	}
//	
//	/**
//	 * ����POST�������ͽӿڣ������Է�ҳ��ȡ�����б�Ϊ��
//	 * https://ip:port/artemis/api/api/resource/v1/regions
//	 */
//	public static String callPostApiGetRegions(){
//		/**
//		 * https://ip:port/artemis/api/resource/v1/regions
//		 * ����API�ĵ����Կ�����,����һ��POST�����Rest�ӿ�, ���Ҵ���Ĳ���ΪJSON�ַ���.
//		 * ArtemisHttpUtil�������ṩ��doPostFormArtemis�������, һ������������ĵ���д�����е���˼. ��Ϊ�ӿ���https,
//		 * ���Ե�һ������path�Ǹ�hashmap����,��putһ��key-value, querysΪ����Ĳ���.
//		 * body ΪJSON�ַ���.
//		 * query������,���Դ���null,accept��contentType��ָ������Ĭ�ϴ�null.
//		 */
//		String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/regions";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("pageNo", "1");
//		paramMap.put("pageSize", "2");
//		String body = JSON.toJSON(paramMap).toString();
//		Map<String, String> path = new HashMap<String, String>();
//		path.put("https://", getCamsApi);
//		String result = ArtemisHttpUtil.doPostStringArtemis(path, body, null, null, "application/json");
//		return result;
//	}
//	
//	/**
//	 * 1.��ȡ��������Ϣ
//	 * /api/resource/v1/regions/root
//	 */
//	public static String postGetRoot(){
//		String url =  "/api/resource/v1/regions/root";
//		Map<String, String> map = new HashMap<String, String>();// post����Form������
//		map.put("treeCode", "0");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * ���� ������ ��ȡ��һ��������Ϣ�б�
//	 */
//	public static String postGetRegions(){
//		String url =  "/api/resource/v1/regions/subRegions";
//		Map<String, String> map = new HashMap<String, String>();// post����Form������
//		map.put("treeCode", "0");
//		map.put("parentIndexCode", "root000000");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * ���������Ż�ȡ������Ϣ
//	 * @return
//	 */
//	public static String postGetRegionInfo(){
//		String url = "/api/resource/v1/region/indexCode/regionInfo";
//		Map<String, String> map = new HashMap<String, String>();// post����Form������
//		map.put("regionIndexCode", "7719de9a-4eb9-4d4a-94cf-c92aa738cdad");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * ��ҳ��ȡ������Ϣ
//	 */
//	public static String postGetRegionsByPage(){
//		String url = "/api/resource/v1/regions";
//		Map<String, String> map = new HashMap<String, String>();// post����Form������
//		map.put("pageNo", "1");
//		map.put("pageSize","4");
//		map.put("treeCode", "0");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	
//	/**
//	 * 2.���������Ż�ȡ��ص��б�
//	 */
//	public static String postGetCameras(){
//		String url = "/api/resource/v1/regions/regionIndexCode/cameras";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("regionIndexCode", "7719de9a-4eb9-4d4a-94cf-c92aa738cdad");
//		paramMap.put("pageNo","1");
//		paramMap.put("pageSize","2");
//		paramMap.put("treeCode","0");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	/**
//	 * ��ҳ��ȡ��ص��б�
//	 */
//	public static String postGetCamerasByPage(){
//		String url = "/api/resource/v1/cameras";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("pageNo","1");
//		paramMap.put("pageSize","2");
//		paramMap.put("treeCode","0");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	/**
//	 * ��ѯ��ص��б�
//	 */
//	public static String searchSpot(){
//		String url = "/api/resource/v1/camera/advance/cameraList";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("pageNo","1");
//		paramMap.put("pageSize","2");
//		paramMap.put("cameraIndexCodes","2");
////		paramMap.put("cameraName","2");
////		paramMap.put("encodeDevIndexCode","2");
////		paramMap.put("regionIndexCode","2");
////		paramMap.put("isCascade","0");
//		paramMap.put("treeCode","0");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	/**
//	 * ���ݼ�ص��Ż�ȡ��ص�����
//	 * @return
//	 */
//	public static String postGetSpotInfo(){
//		String url = "/api/resource/v1/cameras/indexCode";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("cameraIndexCode", "85e1e8ad0c7a474e9882d9978e49101a");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	
//	/**
//	 * 3.���ݼ�ص��Ż�ȡ��Ƶ��
//	 * @param args
//	 * @throws NoSuchAlgorithmException
//	 * @throws UnsupportedEncodingException
//	 * @throws InvalidKeyException
//	 */
//	public static HK_response postGetStream(String cameraIndexCode){
//		String url = "/api/video/v1/cameras/previewURLs";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
////		paramMap.put("cameraIndexCode", "b52cd10cbe8545aeb41498e8462ee949");
//		paramMap.put("cameraIndexCode", cameraIndexCode);
//		paramMap.put("streamType", "0"); //0:������ 1:������ �������Ĭ��Ϊ������
//		paramMap.put("protocol","rtsp"); //��rtsp��:RTSPЭ�� ��rtmp��:RTMPЭ�� ��hls��:HLSЭ�� �������Ĭ��ΪRTSPЭ��
//		paramMap.put("transmode","1"); //0:UDP��1:TCP��Ĭ����TCP��ע��EHOME�豸�ط�ֻ֧��TCP���䣬GB28181 2011����ǰ�汾ֻ֧��UDP����
//		paramMap.put("expand","streamform=ps"); //��ʶ��չ���ݣ���ʽ��key=value�� ���÷������䲥�ſؼ�֧�ֵĽ����ʽѡ����Ӧ�ķ�װ���ͣ� ֧�ֵ����������¼F expand��չ����˵��
//		String result = postApi(url,paramMap);
//		JSONObject response = JSONObject.fromObject(result);
//		Object bean = JSONObject.toBean(response,HK_response.class);
//		HK_response or = (HK_response)bean;
//				
//	    return or;// (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
//	}
//	
////	4.���ݼ�ص��Ž�����̨����
//	public static String ptzsControl(){
//		String url = "/api/video/v1/ptzs/controlling";
//		Map<String, String> paramMap = new HashMap<String, String>();// post����Form������
//		paramMap.put("cameraIndexCode", "85e1e8ad0c7a474e9882d9978e49101a"); //��ص���
//		paramMap.put("action", "0"); //0-��ʼ��1-ֹͣ
//		paramMap.put("command","UP");  // LEFT ��ת RIGHT��ת UP ��ת DOWN ��ת ZOOM_IN ������ ZOOM_OUT �����С LEFT_UP ���� LEFT_DOWN ���� RIGHT_UP ���� RIGHT_DOWN ���� FOCUS_NEAR ����ǰ�� FOCUS_FAR ������� IRIS_ENLARGE ��Ȧ���� IRIS_REDUCE ��Ȧ��С ��������presetIndex���� Ϊ�գ� GOTO_PRESET��Ԥ�õ�
//		paramMap.put("speed","1"); //ȡֵ��ΧΪ1-100��Ĭ��50,�Ǳ���
////		paramMap.put("presetIndex","0"); //Ԥ�õ��ţ�������ͨ����300����
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
////		System.out.print(postGetRoot());
////		System.out.println( postGetRegions());
////		System.out.println(postGetRegionInfo());
////		System.out.println(postGetRegionsByPage());
////		System.out.println(postGetCamerasByPage());
////		System.out.println( postGetCameras());
//		System.out.println( postGetStream("a7d9d1d848d043d5930aa38930cde65e"));
//	}
//	
}
