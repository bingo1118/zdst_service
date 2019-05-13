package com.cloudfire.dao;

import com.cloudfire.entity.LoginEntity;
import com.cloudfire.entity.LoginHttpRsult;

public interface LoginDao {
	public LoginEntity login(String userId);

	public LoginEntity loginToYooSee(String userId, String pwd);

	public boolean addUser(String userId, int privilege, String name);

	public boolean addUser(String userId, int privilege, String name, String pwd, String salt);

	public LoginEntity login2(String userId, String psw);

	/**
	 * @author lzo
	 * @param appId
	 *            用于个推解绑的appId
	 * @param Alias
	 *            用于个推解绑的别名
	 * @return boolean
	 */
	public boolean unBindAliasAll(String appId, String Alias, String cid);

	public boolean unBindAlias(String appId, String Alias, String cid);

	/**
	 * @author lzo
	 * @param appId用于个推绑定appid以及别名和CID
	 * @param Alias
	 * @param cid
	 * @return
	 */
	public boolean bindAlias(String appId, String Alias, String cid);

	/**
	 * @author lzo
	 * @param cid将绑定个推信息入库
	 * 
	 */

	public void savePushBindCid(String userid, String cid, int ifbind);

	public void updateUserAppId(String userId, int appId);

	public int getAppIdByUser(String userId);

	public int loginUser(String userId, String pwd);

	/**
	 * genju yonghuming panduan shifou yi denglu,1:yidenglu,0:wei denglu;
	 * 
	 * @param userId
	 *            yonghuming
	 * @param state
	 *            denglu zhuangtai
	 * @return
	 */
	public int updateLoginState(String userId, int state, String ipaddress);

	/**
	 * genju yonghumming huoqu yonghu shifou denglu zhuangtai.
	 * 
	 * @param userId
	 * @return
	 */
	public int getLoginState(String userId);

	/**
	 * panduan gai yong hu shi fou yi denglu,deng lu fanhui true,fou ze false;
	 * 
	 * @return
	 */
	public boolean charactUser(String userId);

	/**
	 * panduan gai yong hu shi fou yi denglu,deng lu fanhui true,fou ze false;
	 * 
	 * @return
	 */
	public boolean charactUserName(String username);

	/*
	 * @author lzw
	 * 
	 */
	public int elecMeterLogin(String user, String pwd);

	public int elecMeterAddUser(String user, String pwd);

	public int elecMeterSendEmail(String email);

	public int elecMeterCheckEmail(String serialCode, String email);

	/**
	 * 设置用户权限
	 */
	public int updateUserInfo(String userId, String privilege);

	/**
	 * 用于电信easy-iot用户登录获取accessToKen 平台登录: 接口说明：第三方应用调用此接口登入EasyIoT平台，获得鉴权token。
	 * 该token有效期为24小时，24小时过后需重新登录以获取新的token，
	 * 如果在token有效期内成功调用任一平台接口，则会刷新有效期，即从调用接口时间开始， 24小时有效期。
	 */
	public String getEaseIotAccessToKen(String serverId, String password);

	/**
	 * 用于电信easy-iot用户登录获取accessToKen 平台登录: 接口说明：第三方应用调用此接口登入EasyIoT平台，获得鉴权token。
	 * 该token有效期为24小时，24小时过后需重新登录以获取新的token，
	 * 如果在token有效期内成功调用任一平台接口，则会刷新有效期，即从调用接口时间开始， 24小时有效期。
	 */
	public String getEaseIotAccessToKen(String serverId, String password, String ioturl);

	public String getNanJinToKen(String serverId, String password);

	public int getCompanyByUser(String userId);

	public String getSaltByUserId(String userId);

	public boolean unBindIOS(String alias);

	public LoginHttpRsult verify(String userName,String pwd,String privId);

}
