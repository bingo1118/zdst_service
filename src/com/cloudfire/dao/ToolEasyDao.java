package com.cloudfire.dao;

import java.util.List;
import java.util.Map;

import com.cloudfire.entity.EasyIOT;

public interface ToolEasyDao {
	
	/**
	 * 注册订阅地址
     * 向平台注册第三方应用接受消息的回调地址。该订阅地址是平台向第三方应用回调的接口地址固定部分。接口的可变部分参见各回调接口的定义。
	 * @param accessToken accessToken="请填写成功登陆后的accessToken";
	 * @param serverID serverID="请填写用户名";
	 * @param callbackUrl 用于接收后向订阅消息的地址callbackUrl="请填写订阅消息地址";
	 * @return
	 */
	public String subAccessToKen(String accessToken,String serverID,String callbackUrl,String IotUrl);
	
	/**
	 * 设备控制
     * 通过本接口向设备发送控制命令。通过后向接口获得指令的执行结果。
	 * @param accessToken accessToken="请填写成功登陆后的accessToken";
	 * @param serverID serverID="请填写用户名";
	 * @param devSerial devSerial="请填写设备序列号,即IMEI";
	 * @param method method="请填写控制命令id";
	 * @param value ="method"的值
	 * @return
	 */
	public String ackAccessToKen(String accessToken,String serverID,String devSerial,String method,String key,int value,String IotUrl);
	
	/**
	 * @param param 多个参数值
	 * @return
	 */
	public String ackAccessToKens(String accessToken,String serverID,String devSerial,String method,Map<String,Integer> param,String IotUrl);
	
	/**
	 * 查询IoT连接平台
		接口说明：获取所连接的物联网终端连接管理平台信息。
		这个接口主要是向第三方客户应用提供在设备注册接口中输入“connectPointId”字段值所用。
		即，在“单设备注册”、“设备批量注册”接口中，需要传递的 connectPointId值，需要从本接口中获取。
		注：本接口内容仅需获取一次即可，用户可将获取的内容保存起来，后续无需再次调用；
		批量注册时，更无需在循环中反复调用。 
	 */
	public String connectionPlatform(String serverID,String accessToken,String IotUrl);
	
	/**
	 * 查询 IoT 平台服务模式
	 * 接口说明：获取某个IoT连接平台所支持的服务模式。
	 * 服务模式是设备注册时选择的连接模式，例如PSM、eDRX等，
	 * 决定了一个设备在NB-IoT网络中运行的模式。
	 * 每个IoT平台所支持的模式可能不同，
	 * 第三方平台通过本接口查询某个IoT连接平台所支持的业务模式，
	 * 以在设备注册页面中以下拉框的形式显示给用户选择。
	 */
	public String queryServiceMode(String serverID,String accessToken,String iotserverId,String IotUrl);
	
	/**
	 * 单设备注册 
	 * 接口说明：向平台注册单个设备。任何一个设备，只有经过注册并成功后，才能进行控制和订阅消息。
	 * 调用本注册设备接口前，必须获得connectPointId，此Id通过前述的“查询IoT连接平台”接口获得。
	 * @param serverID
	 * @param accessToken
	 * @param devSerial 必选Stringbody设备序列号，目前使用的是模组对应的IMEI。
	 * @param name 可选String(1-256)body设备名称
	 * @param deviceType 必选String(1-256)body设备类型（产品型号），即已创建的产品名称
	 * @param connectPointId必选Stringbody设备连接点ID值携带从“查询IoT连接平台”接口的返回值中某个平台的ID值
	 * @param serviceMode可选Stringbody服务模式名称，即调用“查询 IoT 平台服务模式”接口的返回值中的某个连接模式。缺省为PSM模式。目前“HW-test-iot-117”和“HW-test-iot-112”两个连接点仅支持PSM模式，“ctc-nanjing-iot-137”同时支持PSM和eDRX模式。
	 * @param endUserName可选String(1-256)body终端用户名称，如手机号码等
	 * @param endUserInfo可选String(1-256)body终端用户信息，如手机号码等
	 * @param location可选String(1-1024)body设备的位置
	 * @param longitude可选Floatbody设备所安装位置的经度
	 * @param latitude可选Floatbody设备所安装位置的纬度
	 * @param extend_type可选Stringbody	扩展类型参数，如不确定，请留空
	 * @return
	 */
	public String registeredPlant(String serverID, String accessToken,
			String devSerial, String name, String deviceType,
			String connectPointId, String serviceMode, String endUserName,
			String endUserInfo, String location, String longitude,
			String latitude, String extend_type,String IotUrl);
	
	/**
	 * 更新一个已注册设备的信息。
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param devSerial	设备序列号 = "请填写要更新的设备序列号,即IMEI";
	 * @param name	设备名称，可选 = "请填写新的设备名称";
	 * @param longitude	经度，可选 = "请填写新的经度";
	 * @param latitude	纬度，可选 = "请填写新的纬度";
	 * @return
	 */
	public String updateDevice(String serverID,String accessToken,String devSerial,String name,String longitude,String latitude,String IotUrl);
	
	/**
	 * 删除设备
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param devSerial	设备序列号 = "请填写要更新的设备序列号,即IMEI";
	 * @return
	 */
	public String deleteDevice(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * 设备信息查询
	 * 查询一个已注册设备的详细信息，包括设备的基本信息，当前业务数据，告警数据，以及定时任务等数据。
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param devSerial	设备序列号 = "请填写要更新的设备序列号,即IMEI";
	 * @return
	 */
	public String queryDevice(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * 查询设备类型（产品型号）信息
	 * 通过本接口查询一个设备类型（产品型号）的信息
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param devType	指定设备类型(产品型号)	="请填写设备类型(产品型号)";
	 * @return
	 */
	public String queryDeviceType(String serverID, String accessToken, String devType,String IotUrl);
	
	/**
	 * 查询所有可用的设备类型（产品型号）
	 * 接口说明：查询一个平台帐号下所有可用于设备注册的设备类型（产品型号）
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @return
	 */
	public String queryAllAvailableDeviceTypes(String serverID,String accessToken,String IotUrl);
	
	/**
	 * 查询用户所有设备
	 * 查询一个平台帐号下注册的所有设备
	 * @param serverID 消息头参数serverID = "请填写用户名";
	 * @param accessToken 消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @return
	 */
	public String queryUserAllDevice(String serverID,String accessToken,String IotUrl);
	
	/**
	 * 查询设备业务数据
	 * 接口说明：第三方通过该接口查询设备的业务数据历史记录
	 * @param serverID	消息头参数serverID = "请填写用户名";
	 * @param accessToken	消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param devSerial	设备序列号 = "请填写要更新的设备序列号,即IMEI";
	 * @return
	 */
	public String queryTheDeviceDusinessData(String serverID,String accessToken,String devSerial,String IotUrl);
	
	/**
	 * 取消订阅地址
	 * 向本平台取消第三方平台接受消息的回调地址
	 * @param serverID 消息头参数serverID = "请填写用户名";
	 * @param accessToken 消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @return
	 */
	public String unSubscribe(String serverID,String accessToken,String IotUrl);
	
	/**
	 * 查询订阅地址
	 * 向本平台查询第三方平台接受消息的回调地址。本方法所注册的订阅地址是本平台向该第三方平台回调的接口地址固定部分。接口的可变部分参见各回调接口的定义。
	 * @param serverID 消息头参数serverID = "请填写用户名";
	 * @param accessToken 消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @return
	 */
	public String querySubscribe(String serverID,String accessToken,String IotUrl);
	
	/**
	 * 设备消息通知
	 * 平台通过该接口向第三方应用报告所订阅的设备的最新业务数据。
	 * @param callbackUrl  其中{callbackUrl}为设备消息订阅接口中提供的第三方平台的回调地址
	 * @param devSerial 设备序列号，目前使用的是模组对应的IMEI
	 * @param createTime 设备注册创建时间
	 * @param lastMesssageTime 该设备最后收到设备业务数据时间
	 * @param iotEventTime IoT平台接收到消息的时间
	 * @param serviceData 设备业务数据，参数结构参见以下示例代码。
	 * @return
	 */
	public String deviceMessageNotification(String callbackUrl,String devSerial,String createTime,String lastMesssageTime,String iotEventTime,String serviceData,String IotUrl);
	
	/**
	 * 批量注册
	 * @param serverID 消息头参数serverID = "请填写用户名";
	 * @param accessToken 消息头参数accessToken = "请填写成功登陆后的accessToken";
	 * @param objdev 注册信息List集合
	 * @return
	 */
	public String registrationCompleted(String serverID,String accessToken,List<EasyIOT> objdev,String IotUrl);
	
	/**
	 * @return
	 */
	public String queryService(String hostID,String IotUrl);
	
	/**
	 * 添加 easyIot的数据记录
	 */
	public void addIotEntity(String devSerial,String createTime,String iotEventTime,String serviceId,String IOT_key,String IOT_value,String IotUrl);
	
	/**
	 * 操作阈值记录
	 */
	public void addOperator(String userid,String devMac,String context);
	
	/**
	 * 操作阈值记录
	 */
	public void addOperator(String userid,String devMac,String context,String isOk);
}

