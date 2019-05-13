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
//		ArtemisConfig.host = "111.230.205.112:9016";//"112.65.140.140:1443"; // artemis网关服务器ip端口
//		ArtemisConfig.appKey = "21836658"; //23565114;//"20908044"; // 秘钥appkey
//		ArtemisConfig.appSecret = "wKJEkJ8cm8260ThW9JcQ";//"2XkWa4s0Z4R0X4XbST3u";//"J5kNJ08zxdqImAl8eC2q";// 秘钥appSecret
//	}
//	
//	/**
//	 * 能力开放平台的网站路径
//	 * TODO 路径不用修改，就是/artemis
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
//	 * 调用POST请求类型接口，这里以获取组织列表为例
//	 * https://ip:port/artemis/api/resource/v1/org/orgList
//	 */
//	public static String callPostApiGetOrgList() {
//		/**
//		 * https://ip:port/artemis/api/resource/v1/org/orgList
//		 * 根据API文档可以看出来,这是一个POST请求的Rest接口, 而且传入的参数为JSON字符串.
//		 * ArtemisHttpUtil工具类提供了doPostFormArtemis这个函数, 一共五个参数在文档里写明其中的意思. 因为接口是https,
//		 * 所以第一个参数path是个hashmap类型,请put一个key-value, querys为传入的参数. 
//		 * body 为JSON字符串.
//		 * query不存在,所以传入null,accept和contentType不指定按照默认传null.
//		 */
//		String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/org/orgList";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
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
//	 * 调用POST请求类型接口，这里以分页获取区域列表为例
//	 * https://ip:port/artemis/api/api/resource/v1/regions
//	 */
//	public static String callPostApiGetRegions(){
//		/**
//		 * https://ip:port/artemis/api/resource/v1/regions
//		 * 根据API文档可以看出来,这是一个POST请求的Rest接口, 而且传入的参数为JSON字符串.
//		 * ArtemisHttpUtil工具类提供了doPostFormArtemis这个函数, 一共五个参数在文档里写明其中的意思. 因为接口是https,
//		 * 所以第一个参数path是个hashmap类型,请put一个key-value, querys为传入的参数.
//		 * body 为JSON字符串.
//		 * query不存在,所以传入null,accept和contentType不指定按照默认传null.
//		 */
//		String getCamsApi = ARTEMIS_PATH + "/api/resource/v1/regions";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
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
//	 * 1.获取根区域信息
//	 * /api/resource/v1/regions/root
//	 */
//	public static String postGetRoot(){
//		String url =  "/api/resource/v1/regions/root";
//		Map<String, String> map = new HashMap<String, String>();// post请求Form表单参数
//		map.put("treeCode", "0");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * 根据 区域编号 获取下一级区域信息列表
//	 */
//	public static String postGetRegions(){
//		String url =  "/api/resource/v1/regions/subRegions";
//		Map<String, String> map = new HashMap<String, String>();// post请求Form表单参数
//		map.put("treeCode", "0");
//		map.put("parentIndexCode", "root000000");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * 根据区域编号获取区域信息
//	 * @return
//	 */
//	public static String postGetRegionInfo(){
//		String url = "/api/resource/v1/region/indexCode/regionInfo";
//		Map<String, String> map = new HashMap<String, String>();// post请求Form表单参数
//		map.put("regionIndexCode", "7719de9a-4eb9-4d4a-94cf-c92aa738cdad");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	/**
//	 * 分页获取区域信息
//	 */
//	public static String postGetRegionsByPage(){
//		String url = "/api/resource/v1/regions";
//		Map<String, String> map = new HashMap<String, String>();// post请求Form表单参数
//		map.put("pageNo", "1");
//		map.put("pageSize","4");
//		map.put("treeCode", "0");
//		String result =postApi(url,map);
//		return result;
//	}
//	
//	
//	/**
//	 * 2.根据区域编号获取监控点列表
//	 */
//	public static String postGetCameras(){
//		String url = "/api/resource/v1/regions/regionIndexCode/cameras";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
//		paramMap.put("regionIndexCode", "7719de9a-4eb9-4d4a-94cf-c92aa738cdad");
//		paramMap.put("pageNo","1");
//		paramMap.put("pageSize","2");
//		paramMap.put("treeCode","0");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	/**
//	 * 分页获取监控点列表
//	 */
//	public static String postGetCamerasByPage(){
//		String url = "/api/resource/v1/cameras";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
//		paramMap.put("pageNo","1");
//		paramMap.put("pageSize","2");
//		paramMap.put("treeCode","0");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	/**
//	 * 查询监控点列表
//	 */
//	public static String searchSpot(){
//		String url = "/api/resource/v1/camera/advance/cameraList";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
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
//	 * 根据监控点编号获取监控点详情
//	 * @return
//	 */
//	public static String postGetSpotInfo(){
//		String url = "/api/resource/v1/cameras/indexCode";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
//		paramMap.put("cameraIndexCode", "85e1e8ad0c7a474e9882d9978e49101a");
//		String result = postApi(url,paramMap);
//		return result;
//	}
//	
//	
//	/**
//	 * 3.根据监控点编号获取视频流
//	 * @param args
//	 * @throws NoSuchAlgorithmException
//	 * @throws UnsupportedEncodingException
//	 * @throws InvalidKeyException
//	 */
//	public static HK_response postGetStream(String cameraIndexCode){
//		String url = "/api/video/v1/cameras/previewURLs";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
////		paramMap.put("cameraIndexCode", "b52cd10cbe8545aeb41498e8462ee949");
//		paramMap.put("cameraIndexCode", cameraIndexCode);
//		paramMap.put("streamType", "0"); //0:主码流 1:子码流 参数不填，默认为主码流
//		paramMap.put("protocol","rtsp"); //“rtsp”:RTSP协议 “rtmp”:RTMP协议 “hls”:HLS协议 参数不填，默认为RTSP协议
//		paramMap.put("transmode","1"); //0:UDP，1:TCP，默认是TCP。注：EHOME设备回放只支持TCP传输，GB28181 2011及以前版本只支持UDP传输
//		paramMap.put("expand","streamform=ps"); //标识扩展内容，格式：key=value， 调用方根据其播放控件支持的解码格式选择相应的封装类型； 支持的内容详见附录F expand扩展内容说明
//		String result = postApi(url,paramMap);
//		JSONObject response = JSONObject.fromObject(result);
//		Object bean = JSONObject.toBean(response,HK_response.class);
//		HK_response or = (HK_response)bean;
//				
//	    return or;// (OneNetResponse)JSONObject.toBean(response,OneNetResponse.class);
//	}
//	
////	4.根据监控点编号进行云台控制
//	public static String ptzsControl(){
//		String url = "/api/video/v1/ptzs/controlling";
//		Map<String, String> paramMap = new HashMap<String, String>();// post请求Form表单参数
//		paramMap.put("cameraIndexCode", "85e1e8ad0c7a474e9882d9978e49101a"); //监控点编号
//		paramMap.put("action", "0"); //0-开始，1-停止
//		paramMap.put("command","UP");  // LEFT 左转 RIGHT右转 UP 上转 DOWN 下转 ZOOM_IN 焦距变大 ZOOM_OUT 焦距变小 LEFT_UP 左上 LEFT_DOWN 左下 RIGHT_UP 右上 RIGHT_DOWN 右下 FOCUS_NEAR 焦点前移 FOCUS_FAR 焦点后移 IRIS_ENLARGE 光圈扩大 IRIS_REDUCE 光圈缩小 以下命令presetIndex不可 为空： GOTO_PRESET到预置点
//		paramMap.put("speed","1"); //取值范围为1-100，默认50,非必须
////		paramMap.put("presetIndex","0"); //预置点编号，整数，通常在300以内
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
